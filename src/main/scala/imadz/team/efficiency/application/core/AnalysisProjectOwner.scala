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

final case class GitProjectProcessResult(project: GitProject, error: Option[String] = None, warning: Option[String] = None) extends ProjectOwnerCommand {
  def success: Boolean = error.isEmpty
}

class AnalysisProjectOwner(context: ActorContext[ProjectOwnerCommand], id: Int, key: String) extends AbstractBehavior[ProjectOwnerCommand](context) {
  private var workers = Map.empty[GitProject, ActorRef[WorkerCommand]]

  override def onMessage(msg: ProjectOwnerCommand): Behavior[ProjectOwnerCommand] = Behaviors.receive { (context, message) =>
    context.log.info("Receiving message {}", message)
    message match {
      case Identity(replyTo) =>
        context.log.info("Project Owner {} @ {} identified itself", id, key)
        replyTo ! IdentityResponse(id, key)
        Behaviors.same
      case CreateAnalysisProject(project, replyTo) =>
        if (project.gitProjects.length > 100) {
          replyTo ! AnalysisProjectCreateResponse(error = Some("too many git projects"))
          Behaviors.same
        } else {
          replyTo ! AnalysisProjectCreateResponse(id = Some(id))
          setupWorkers(context, project)
          busy()
          Behaviors.same
        }
    }
  }

  private def setupWorkers(context: ActorContext[ProjectOwnerCommand], project: AnalysisProject): Unit = {
    //FIXME: the order of the list might makes project id different
    project.gitProjects.zipWithIndex.foreach { i =>
      context.log.debug("spawning workers for {} ...", i)
      val gitProject = GitProject(i._1.repositoryUrl, i._1.branch, i._2, s"${project.basedir}/${id}")
      if (workers.contains(gitProject)) {
        workers(gitProject) ! KickOff
      } else {
        val worker = context.spawn(AnalysisProjectWorker(context.self, gitProject, i._2), s"worker-${i._2}")
        context.watch(worker)
        workers += (gitProject -> worker)
        worker ! KickOff
      }
    }
  }

  private def busy(): Behavior[ProjectOwnerCommand] = Behaviors.receive { (context, message) =>
    message match {
      // FIXME: add more logic to switch back to idle
      case anyCommand =>
        context.log.info("Project Owner {} @ {} is busy, ignoring {}", id.toString, key, anyCommand.toString)
        Behaviors.same
    }
  }

}

object AnalysisProjectOwner {
  def apply(id: Int, key: String): Behavior[ProjectOwnerCommand] = Behaviors.setup(context => new AnalysisProjectOwner(context, id, key))
}
