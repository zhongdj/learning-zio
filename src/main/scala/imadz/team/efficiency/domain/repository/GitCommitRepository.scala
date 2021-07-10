package imadz.team.efficiency.domain.repository

import imadz.team.efficiency.domain.Errors.DomainError
import imadz.team.efficiency.domain.entity.git.GitCommit
import zio.IO

trait GitCommitRepository {
  def save(commit: GitCommit): IO[DomainError, Unit]

}
