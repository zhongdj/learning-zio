package imadz.team.efficiency.infrastructure

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import imadz.team.efficiency.infrastructure.controller.AnalysisProjectDelegate.Command
import imadz.team.efficiency.infrastructure.controller.{AnalysisProjectDelegate, AnalysisProjectRoutes}

import scala.util.{Failure, Success}

object TeamEfficiencyAnalysisApp {

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val taskManager: ActorRef[Command] = context.spawn(AnalysisProjectDelegate(), "analysis-project-delegate")
      val route: Route = new AnalysisProjectRoutes(taskManager: ActorRef[Command])(context.system).route
      startHttpServer(route)(context.system)
      Behaviors.empty
    }
    ActorSystem[Nothing](rootBehavior, "TeamEfficiencyAnalysisTool")
  }

  def startHttpServer(route: Route)(implicit system: ActorSystem[Nothing]): Unit = {
    import system.executionContext
    Http()
      .newServerAt("0.0.0.0", getPort(system))
      .bind(route)
      .onComplete {
        case Success(binding) =>
          val address = binding.localAddress
          system.log.info("Team Efficiency Analysis Server Started @ http://{}:{}", address.getHostString, address.getPort)
        case Failure(ex) =>
          system.log.error("Failed to bind to Endpoint", ex)
      }
  }

  private def getPort(system: ActorSystem[Nothing]) = {
    system.settings.config.getInt("imadz.server.port")
  }
}
