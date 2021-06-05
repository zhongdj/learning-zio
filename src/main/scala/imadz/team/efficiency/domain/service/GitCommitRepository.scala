package imadz.team.efficiency.domain.service

import imadz.team.efficiency.domain.Errors.DomainError
import imadz.team.efficiency.domain.entity.GitCommit
import zio.IO

trait GitCommitRepository {
  def save(commit: GitCommit): IO[DomainError, Unit]

}
