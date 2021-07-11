package imadz.team.efficiency.application.core

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import imadz.team.efficiency.domain.entity.git.GitProject
import imadz.team.efficiency.domain.entity.mgt.AnalysisProject

sealed trait ProjectOwnerCommand

final case class Identity(replyTo: ActorRef[IdentityResponse]) extends ProjectOwnerCommand

final case class IdentityResponse(id: Int, key: String)

final case class CreateAnalysisProject(project: AnalysisProject, replyTo: ActorRef[AnalysisProjectCreateResponse]) extends ProjectOwnerCommand

final case class AnalysisProjectCreateResponse(id: Option[Int] = None, error: Option[String] = None)

final case class UpdateAnalysisProject(replyTo: ActorRef[UpdateProjectResponse]) extends ProjectOwnerCommand

final case class UpdateProjectResponse()

final case class WorkerTerminated(project: GitProject) extends ProjectOwnerCommand

final case class GitProjectProcessResult(project: GitProject, error: Option[String] = None, warning: Option[String] = None) extends ProjectOwnerCommand {
  def success: Boolean = error.isEmpty
}

class AnalysisProjectOwner(context: ActorContext[ProjectOwnerCommand], id: Int, key: String) extends AbstractBehavior[ProjectOwnerCommand](context) {
  private var workers = Map.empty[GitProject, ActorRef[WorkerCommand]]

  override def onMessage(msg: ProjectOwnerCommand): Behavior[ProjectOwnerCommand] = idle

  private def idle(): Behavior[ProjectOwnerCommand] = Behaviors.receive { (context, message) =>
    context.log.info("Receiving message {} in idle state", message)
    message match {
      case Identity(replyTo) =>
        context.log.info("Project Owner {} @ {} identified itself", id, key)
        workers.values foreach (_ ! KickOff)
        replyTo ! IdentityResponse(id, key)
        Behaviors.same
      case CreateAnalysisProject(project, replyTo) =>
        if (project.gitProjects.length > 100) {
          replyTo ! AnalysisProjectCreateResponse(error = Some("too many git projects"))
          Behaviors.same
        } else {
          setupWorkers(context, project)
          replyTo ! AnalysisProjectCreateResponse(id = Some(id))
          busy()
        }
      case GitProjectProcessResult(project, error, warning) =>
        context.log.info("receiving result for {}", project)
        error.foreach(context.log.error)
        warning.foreach(context.log.warn)
        Behaviors.same
      case WorkerTerminated(project) =>
        workers -= project
        Behaviors.empty
    }
  }

  private def busy(): Behavior[ProjectOwnerCommand] = Behaviors.receive { (context, message) =>
    context.log.info("Receiving message {} in busy state", message)
    message match {
      case Identity(replyTo) =>
        replyTo ! IdentityResponse(id, key)
        Behaviors.same
      case CreateAnalysisProject(project, replyTo) =>
        replyTo ! AnalysisProjectCreateResponse(error = Some(s"Project ${project} is in progress"))
        Behaviors.same
      case r@GitProjectProcessResult(project, error, warning) =>
        context.log.info("receiving result for {}", r)
        error.foreach(context.log.error)
        warning.foreach(context.log.warn)
        idle
      case WorkerTerminated(project) =>
        workers -= project
        Behaviors.empty
      case anyCommand =>
        context.log.info("Project Owner {} @ {} is busy, ignoring {}", id.toString, key, anyCommand.toString)
        Behaviors.same
    }
  }

  private def setupWorkers(context: ActorContext[ProjectOwnerCommand], project: AnalysisProject): Unit = {
    //FIXME: the order of the list might makes project id different
    project.gitProjects.zipWithIndex.foreach { i =>
      val gitProject = GitProject(i._1.repositoryUrl, i._1.branch, i._2, s"${project.basedir}/${id}")
      if (workers.contains(gitProject)) {
        kickOffWorker(workers(gitProject))
      } else {
        val worker = context.spawn(AnalysisProjectWorker(context.self, gitProject, i._2), s"worker-${i._2}")
        context.watchWith(worker, WorkerTerminated(gitProject))
        context.log.debug(s"spawning workers for ${i}:{} ...", worker)
        workers += (gitProject -> worker)
        kickOffWorker(worker)
      }
    }
  }

  private def kickOffWorker(worker: ActorRef[WorkerCommand]) = worker ! KickOff

}

object AnalysisProjectOwner {
  def apply(id: Int, key: String): Behavior[ProjectOwnerCommand] = Behaviors.setup(context => new AnalysisProjectOwner(context, id, key))
}
