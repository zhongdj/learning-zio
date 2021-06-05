package imadz.team.efficiency.domain.service

import imadz.team.efficiency.domain.Errors.{DomainError, GitCommitExtractError}
import imadz.team.efficiency.domain.EventPublisher
import imadz.team.efficiency.domain.entity.{GitCommit, GitCommitCreated}
import imadz.team.efficiency.domain.service.git.CommitExtractor
import zio.IO
import zio.stream._

class ReadGitCommitService(repository: GitCommitRepository, eventPublisher: EventPublisher, commitExtractor: CommitExtractor) {

  def toGitCommitEvent: GitCommit => GitCommitCreated = GitCommitCreated

  def extractGitCommits(projectDir: String): IO[DomainError, Unit] = {
    commitExtractor.extract(projectDir)
      .mapError(e => GitCommitExtractError(e.toString))
      .tap(repository.save).map(toGitCommitEvent)
      .tap(eventPublisher.publish("GitCommit"))
      .run(ZSink.drain)
  }
}
