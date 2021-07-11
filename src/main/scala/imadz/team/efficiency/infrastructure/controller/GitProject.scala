package imadz.team.efficiency.infrastructure.controller

import imadz.team.efficiency.infrastructure.controller.AnalysisProjectDelegate.AnalyticProjectCreated
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class AnalyticProject(gitProjects: List[GitProject], options: Option[AnalyticProjectOptions] = Some(AnalyticProjectOptions.defaultOptions)) {
  def getOptions: AnalyticProjectOptions = options.getOrElse(AnalyticProjectOptions.defaultOptions)

  import java.security.MessageDigest

  private def md5(s: String) = MessageDigest.getInstance("MD5")
    .digest(s.getBytes)
    .map(i => Integer.toHexString((i & 0xFF) | 0x100).substring(1,3))
    .mkString

  def hashKey(): String = md5(gitProjects
    .map(p => s"${p.repositoryUrl}:${p.branch}")
    .sorted
    .mkString(","))
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