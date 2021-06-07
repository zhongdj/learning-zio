package imadz.team.efficiency.domain.processor

import imadz.team.efficiency.domain.Errors.{DomainError, GitDiffTreeExtractError}
import imadz.team.efficiency.domain.EventPublisher
import imadz.team.efficiency.domain.entity.{GitCommitCreated, TreeEntryEvent}
import imadz.team.efficiency.domain.service.GitError
import imadz.team.efficiency.domain.service.git.GitCommands.diff_tree_full_index_r_M_C
import imadz.team.efficiency.domain.service.git._
import imadz.team.efficiency.domain.service.shell.shellExecMap
import zio.IO
import zio.stream.ZSink.foldLeft

class CommitEntryOpProcessor(eventPublisher: EventPublisher) {

  def processCommitCreated(created: GitCommitCreated): IO[DomainError, Unit] = {
    for {
      entryEvents <- extractEntryOps(created).mapError(e => GitDiffTreeExtractError(e.message))
    } yield entryEvents.foreach(eventPublisher.publish("TreeEntry"))
  }

  private def extractEntryOps(created: GitCommitCreated): IO[GitError, List[TreeEntryEvent]] = {
    shellExecMap(diff_tree_full_index_r_M_C(created.projectDir)(created.commit.id), GitTreeDiffParser.parseTreeDiffContent)
      .run(foldLeft(nils[TreeEntryEvent])(cons[TreeEntryEvent]))
  }

  private def nils[T] = List[T]()

  private def cons[T]: (List[T], T) => List[T] = (xs, x) => x :: xs
}

