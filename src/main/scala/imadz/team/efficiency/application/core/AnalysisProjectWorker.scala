package imadz.team.efficiency.application.core

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import imadz.team.efficiency.application.task.GitCloneOrUpdateTask
import imadz.team.efficiency.domain.entity.git.GitProject
import zio.IO

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

sealed trait WorkerCommand

case object KickOff extends WorkerCommand

class AnalysisProjectWorker(parent: ActorRef[ProjectOwnerCommand], context: ActorContext[WorkerCommand], project: GitProject, seq: Int) extends AbstractBehavior[WorkerCommand](context) {
  context.log.info("Worker {} is created.", this)

  override def onMessage(msg: WorkerCommand): Behavior[WorkerCommand] = {
    msg match {
      case KickOff =>
        context.log.info("Evaluating project {}.", project)
        evaluateTasks(orchestrateTasks)(parent)(context.executionContext)
        Behaviors.same
      case any =>
        context.log.info("Worker receives {}.", any)
        Behaviors.same
    }
  }

  private def orchestrateTasks: GitProject => IO[AnalysisTaskError, Unit] = p => GitCloneOrUpdateTask(p).execute()

  private val runtime = zio.Runtime.default

  private def evaluateTasks(tasks: GitProject => IO[AnalysisTaskError, Unit])(replyTo: ActorRef[GitProjectProcessResult])(implicit ec: ExecutionContextExecutor): Unit = {
    runtime.unsafeRunToFuture(tasks(project)).future.onComplete {
      case Success(_) =>
        replyTo ! GitProjectProcessResult(project)
      case Failure(ex) =>
        replyTo ! GitProjectProcessResult(project, error = Some(ex.getMessage))
    }
  }
}

object AnalysisProjectWorker {
  def apply(parent: ActorRef[ProjectOwnerCommand], project: GitProject, seq: Int): Behavior[WorkerCommand] = Behaviors.setup(context => new AnalysisProjectWorker(parent, context, project, seq))
}
