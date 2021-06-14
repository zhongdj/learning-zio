package imadz.team.efficiency.infrastructure

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import imadz.team.efficiency.domain.service.CloneGitProjectService
import zio.IO

import scala.util.{Failure, Success}

object AnalysisProjectManager {

  trait Command

  final case class CreateProject(project: AnalyticProject, replyTo: ActorRef[AnalyticProjectCreated]) extends Command

  case class AnalyticProjectCreated(id: Option[Int] = None, error: Option[String] = None)

  val cloneService = new CloneGitProjectService
  val runtime = zio.Runtime.default

  def apply(): Behavior[Command] = Behaviors.receive[Command] { (context, command) =>
    implicit val ec = context.executionContext

    command match {
      case CreateProject(project, replyTo) =>
        context.log.info(s"Analytic Project ${project} is created.")
        val io: IO[Throwable, Unit] =
          cloneService.cloneOrUpdate("/Users/zhongdj/.git-stats-tasks/2/git-stats-backend", "git@github.com:zhongdj/git-stats-backend.git", "master")
            .mapError(e => new RuntimeException(e.msg))
        runtime.unsafeRunToFuture(io).future.onComplete {
          case Success(_) =>
            replyTo ! AnalyticProjectCreated(id = Some(1))
          case Failure(ex) =>
            replyTo ! AnalyticProjectCreated(error = Some(ex.getMessage))
        }
    }
    Behaviors.same
  }

}
