package imadz.team.efficiency.application.core

import zio.IO
case class AnalysisTaskError(taskName: String, code: String, message: String) extends Exception
trait AnalysisTask {
  def execute(): IO[AnalysisTaskError, Unit]
}
