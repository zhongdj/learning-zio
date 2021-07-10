package imadz.team.efficiency.infrastructure.repository

import imadz.team.efficiency.domain.Errors
import imadz.team.efficiency.domain.entity.git.GitCommit
import imadz.team.efficiency.domain.repository.GitCommitRepository
import zio.IO

class MySQLGitCommitRepository extends GitCommitRepository {
  override def save(commit: GitCommit): IO[Errors.DomainError, Unit] = ???
}
