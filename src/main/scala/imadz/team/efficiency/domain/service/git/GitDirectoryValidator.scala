package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.Errors.{DomainError, UnwantedGitRepository}
import imadz.team.efficiency.domain.service.git.GitCommands._
import imadz.team.efficiency.domain.service.shell.shellExecFold
import zio.IO
import zio.stream.UStream

trait GitDirectoryValidator {

  def matchGitProject(workdir: String, repositoryUrl: String): IO[DomainError, Boolean] =
    shellExecFold(git_config_get(workdir, conf_remote_origin_url), matchRepositoryParser(repositoryUrl))

  private def matchRepositoryParser(repositoryUrl: String): UStream[String] => IO[DomainError, Boolean] = repositories =>
    repositories.filter(_.equals(repositoryUrl))
      .runHead
      .flatMap {
        case None => IO.fail(UnwantedGitRepository(s"wanted repo: ${repositoryUrl}, but found: ${repositories}"))
        case _ => IO.succeed(true)
      }

}
