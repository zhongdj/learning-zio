package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.service.git.GitCommands.git_clone
import imadz.team.efficiency.domain.service.shell.shellExecUnit
import imadz.team.efficiency.domain.service.{DirectoryAccessDenied, FailedToMkDirectory, GitCloneNonEmptyDirectoryError, GitError}
import zio.IO

import java.io.File

trait GitClone extends GitCommandErrorParser {

  def cloneGitRepo(workdir: String, repositoryUrl: String, branch: String): IO[GitError, Unit] = {
    val repoDir = new File(workdir)
    prepareDirectory(repoDir)
      .zipRight(processClone(repoDir, repositoryUrl, branch))
  }

  private val r = """.*/(.*?)(?:\.git)?""".r

  private def projectOf(repositoryUrl: String): String = repositoryUrl match {
    case r(projectName) => projectName
  }

  private def prepareDirectory(repoDir: File) = {
    IO(repoDir.exists())
      .mapError(e => DirectoryAccessDenied(e.getMessage))
      .flatMap {
        case true =>
          IO.fail[GitError](GitCloneNonEmptyDirectoryError(s"Dir is not empty: ${repoDir.getAbsolutePath}"))
        case false =>
          init(repoDir)
      }
  }

  private def init(repoDir: File): IO[GitError, Unit] = {
    IO(!repoDir.mkdirs())
      .orElseFail(FailedToMkDirectory(s"Failed to create folder: ${repoDir.getAbsolutePath}"))
      .unit
  }

  private def processClone(repoDir: File, repositoryUrl: String, branch: String): IO[GitError, Unit] =
    shellExecUnit(git_clone(repoDir.getAbsolutePath, repositoryUrl, branch), errorParser)

}
