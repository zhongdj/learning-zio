package imadz.team.efficiency.application.task

import imadz.team.efficiency.application.core.{AnalysisTask, AnalysisTaskError}
import imadz.team.efficiency.domain.entity.git.GitProject
import imadz.team.efficiency.domain.service.CloneGitProjectService
import zio.IO

case class GitCloneOrUpdateTask(project: GitProject) extends AnalysisTask {
  val cloneService = new CloneGitProjectService
  override def execute(): IO[AnalysisTaskError, Unit] =
    cloneService.cloneOrUpdate(project.dir, project.url, project.branch)
      .mapError(e => AnalysisTaskError("GitCloneOrUpdateTask", "", e.msg))

}
