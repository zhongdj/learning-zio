package imadz.team.efficiency.application.service

import imadz.team.efficiency.domain.Errors.DomainError
import imadz.team.efficiency.domain.service.ReadGitCommitService
import zio.IO

case class GitRepository(repositoryUrl: String, branch: String, local: Boolean = false, profile: Option[String] = None, excludes: List[String] = Nil)

case class CreateGitProjectReq(taskWorkspaceDir: String, repository: GitRepository)

case class CreateGitProjectService(commitService: ReadGitCommitService) extends AppService[CreateGitProjectReq, IO[AppError, Unit]] {

  type Dir = String

  override def execute(req: CreateGitProjectReq): IO[AppError, Unit] = {
    resetGitProject(req.taskWorkspaceDir, projectOf(req.repository.repositoryUrl))
      .orElse(cloneGitProject(req))
      .flatMap(loadCommits(req))
  }

  private def loadCommits(req: CreateGitProjectReq)(dir: Dir): IO[AppError, Unit] =
    commitService.extractGitCommits(dir)
      .mapError(refineError(req))

  private def refineError(req: CreateGitProjectReq): DomainError => AppError = {
    e => AppError("Create_Project_Failed", s"Failed to create project: ${req.repository.repositoryUrl}/${req.repository.branch}. \n ${e.msg}")
  }

  private def resetGitProject(workspace: Dir, projectName: String): IO[AppError, Dir] = ???

  private def cloneGitProject(req: CreateGitProjectReq): IO[AppError, Dir] = ???

  private val r = """.*/(.*?)(?:\.git)?""".r

  private def projectOf(repositoryUrl: String): String = repositoryUrl match {
    case r(projectName) => projectName
  }
}
