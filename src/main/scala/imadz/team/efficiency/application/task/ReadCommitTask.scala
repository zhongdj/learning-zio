package imadz.team.efficiency.application.task

import imadz.team.efficiency.application.core.{AnalysisTask, AnalysisTaskError}
import zio.IO

class ReadCommitTask extends AnalysisTask {
  override def execute(): IO[AnalysisTaskError, Unit] = ???
}
