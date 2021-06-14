package imadz.team.efficiency.infrastructure

import imadz.team.efficiency.infrastructure.AnalysisProjectManager.AnalyticProjectCreated
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class AnalyticProject(gitProjects: List[GitProject], options: Option[AnalyticProjectOptions] = Some(AnalyticProjectOptions.defaultOptions)) {
  def getOptions: AnalyticProjectOptions = options.getOrElse(AnalyticProjectOptions.defaultOptions)
}

final case class AnalyticProjectOptions(interval: Int, repeatedCycle: Option[Int] = None)

object AnalyticProjectOptions {
  def defaultOptions: AnalyticProjectOptions = AnalyticProjectOptions(30)
}

final case class GitProject(repositoryUrl: String,
                            branch: String,
                            excludes: Option[List[String]] = Some(Nil),
                            local: Option[Boolean] = Some(false)) {
  def getExcludes: List[String] = excludes.getOrElse(Nil)
  def getLocal: Boolean = local.getOrElse(false)
}

object AnalyticProject {
  implicit val analyticProjectCreatedJsonFormat: RootJsonFormat[AnalyticProjectCreated] = jsonFormat2(AnalyticProjectCreated)
  implicit val gitProjectJsonFormat: RootJsonFormat[GitProject] = jsonFormat4(GitProject)
  implicit val analyticProjectOptionsJsonFormat: RootJsonFormat[AnalyticProjectOptions] = jsonFormat2(AnalyticProjectOptions.apply)
  implicit val analyticProjectJsonFormat: RootJsonFormat[AnalyticProject] = jsonFormat2(AnalyticProject.apply)
}