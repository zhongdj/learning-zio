package imadz.team.efficiency.domain.service

import imadz.team.efficiency.domain.Errors.{CloneGitRepositoryError, DomainError, FileOrDirectoryAccessDenied, ResetGitRepositoryError}
import imadz.team.efficiency.domain.service.git.{GitClone, GitDirectoryValidator, GitUpdate}
import zio.IO

import java.io.File

class CloneGitProjectService extends GitDirectoryValidator with GitUpdate with GitClone {

  def cloneOrUpdate(workdir: String, repositoryUrl: String, branch: String): IO[DomainError, Unit] = {
    import CloneGitProjectService._
    validateGitWorkDir(workdir, repositoryUrl)
      .map{
        case false => CloneStrategy(workdir, repositoryUrl, branch)
        case true => UpdateStrategy(workdir, repositoryUrl, branch)
      }
      .flatMap(_.execute)
  }

  private def validateGitWorkDir(workdir: String, repositoryUrl: String): IO[DomainError, Boolean] =
    isGitDir(workdir, repositoryUrl)

  private def isGitDir(workdir: String, repositoryUrl: String): IO[DomainError, Boolean] =
    IO(new File(workdir).exists())
      .mapError(e => FileOrDirectoryAccessDenied(s"File access denied: $workdir due to error: ${e.getMessage}"))
      .flatMap {
        case true => matchGitProject(workdir, repositoryUrl)
        case _ => IO.succeed(false)
      }

  private object CloneGitProjectService {

    sealed trait ResetGitRepoStrategy {
      def execute: IO[DomainError, Unit]
    }

    case class UpdateStrategy(workdir: String, repositoryUrl: String, branch: String) extends ResetGitRepoStrategy {
      override def execute: IO[DomainError, Unit] = updateHard(workdir, branch).mapError(e => ResetGitRepositoryError(e.message))
    }

    case class CloneStrategy(workdir: String, repositoryUrl: String, branch: String) extends ResetGitRepoStrategy {
      override def execute: IO[DomainError, Unit] = cloneGitRepo(workdir, repositoryUrl, branch)
        .mapError(e => CloneGitRepositoryError(e.message))
    }

  }
}


