package imadz.team.efficiency.infrastructure.controller

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import imadz.team.efficiency.infrastructure.controller.AnalysisProjectDelegate.{AnalyticProjectCreated, Command, CreateProject}
import imadz.team.efficiency.infrastructure.controller.AnalyticProject._

import scala.concurrent.Future

class AnalysisProjectRoutes(projectManager: ActorRef[Command])(implicit system: ActorSystem[Nothing]) {

  implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("imadz.routes.ask-timeout"))

  private def createAnalyticProject(project: AnalyticProject): Future[AnalyticProjectCreated] =
    projectManager ? (CreateProject(project, _))

  val route: Route = pathPrefix("v1") {
    concat(
      pathPrefix("analyticProjects") {
        post {
          entity(as[AnalyticProject]) { project =>
            onSuccess(createAnalyticProject(project)) { analyticProjectCreated =>
              complete((StatusCodes.Created, analyticProjectCreated))
            }
          }
        }
      }
    )
  }

}

