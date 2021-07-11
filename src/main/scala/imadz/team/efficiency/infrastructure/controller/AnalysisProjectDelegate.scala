package imadz.team.efficiency.infrastructure.controller

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.util.Timeout
import imadz.team.efficiency.application.core._
import imadz.team.efficiency.domain.entity.mgt.{AnalysisProject, AnalysisProjectOptions}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object AnalysisProjectDelegate {

  trait Command
  final case class CreateProject(project: AnalyticProject, replyTo: ActorRef[AnalyticProjectCreated]) extends Command
  final case class CreateAnalyticProjectIntermediate(id: Option[Int] = None, error: Option[String] = None, replyTo: ActorRef[AnalyticProjectCreated]) extends Command
  final case class AnalyticProjectCreated(id: Option[Int] = None, error: Option[String] = None) extends Command

  private var projectOwners = Map.empty[String, ActorRef[ProjectOwnerCommand]]

  def apply(): Behavior[Command] = Behaviors.receive[Command] { (context, command) =>
    context.log.info("receiving: {}", command)
    lazy val gitRepoBasedir = context.system.settings.config.getString("imadz.git.repo.basedir")
    implicit val system: ActorSystem[Nothing] = context.system
    implicit val timeout: Timeout = Timeout.create(context.system.settings.config.getDuration("imadz.routes.ask-timeout"))
    implicit val ec: ExecutionContextExecutor = context.executionContext

    command match {
      case CreateProject(project, replyTo) =>
        val ownerKey = project.hashKey()
        if (projectOwners.contains(ownerKey)) {
          askIdentity(context, ownerKey, replyTo)
        } else {
          // FIXME: add persistence for project, and then replace 1 with id
          // FIXME: add basedir, project name and workdir logics
          val owner: ActorRef[ProjectOwnerCommand] = context.spawn(AnalysisProjectOwner(1, ownerKey), s"ProjectOwner-${ownerKey}")
          context.watch(owner)
          projectOwners += (ownerKey -> owner)
          askToCreateProject(context, gitRepoBasedir, project, owner, replyTo)
        }
      case CreateAnalyticProjectIntermediate(id, error, replyTo) =>
        error.map(msg => context.log.error(msg))
          .getOrElse(context.log.info("Project created with id = {}", id.toString))
        context.log.info("AnalyticProjectCreated is fired to {}", replyTo)
        replyTo ! AnalyticProjectCreated(id, error)
    }
    Behaviors.same
  }

  private def askToCreateProject(context: ActorContext[Command], gitRepoBasedir: => String, project: AnalyticProject, owner: ActorRef[ProjectOwnerCommand], replyTo: ActorRef[AnalyticProjectCreated])(implicit timeout: Timeout)= {
    context.log.info("asking {}", owner)
    owner ! createAnalysisProjectReq(project)(gitRepoBasedir)(context.self.unsafeUpcast)
    context.ask(owner, createAnalysisProjectReq(project)(gitRepoBasedir)(_)) {
      case Success(AnalysisProjectCreateResponse(id, error)) =>
        CreateAnalyticProjectIntermediate(id, error.map(msg => s"Project create failed: ${project} /nCaused by ${msg}"), replyTo)
      case Failure(ex) =>
        CreateAnalyticProjectIntermediate(None, Some(s"Unexpected error occurred while creating analysis project Caused by: \n${ex.getMessage}"), replyTo)
    }
  }

  private def askIdentity(context: ActorContext[Command], ownerKey: String, replyTo: ActorRef[AnalyticProjectCreated])(implicit timeout: Timeout) = {
    context.log.info("Project Owner {} found", ownerKey)
    val ref: ActorRef[ProjectOwnerCommand] = projectOwners(ownerKey)
    context.ask(ref, ref => Identity(ref)) {
      case Success(response) =>
        CreateAnalyticProjectIntermediate(Some(response.id), None, replyTo)
      case Failure(ex) =>
        CreateAnalyticProjectIntermediate(error = Some(ex.getMessage), replyTo = replyTo)
    }
  }

  private def createAnalysisProjectReq(project: AnalyticProject)(gitRepoBasedir: => String)(ref: ActorRef[AnalysisProjectCreateResponse]) = {
    CreateAnalysisProject(AnalysisProject(project.gitProjects, AnalysisProjectOptions(project.options.get.interval, project.options.get.repeatedCycle), gitRepoBasedir), ref)
  }
}
