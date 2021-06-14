package imadz.team.efficiency.infrastructure

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object AnalysisProjectManager {

  trait Command

  final case class CreateProject(project: AnalyticProject, replyTo: ActorRef[AnalyticProjectCreated]) extends Command

  case class AnalyticProjectCreated(id: Int)

  def apply(): Behavior[Command] = Behaviors.receive[Command] { (context, command) =>
    command match {
      case CreateProject(project, replyTo) =>
        context.log.info(s"Analytic Project ${project} is created.")
        replyTo ! AnalyticProjectCreated(1)
    }
    Behaviors.same
  }

}
