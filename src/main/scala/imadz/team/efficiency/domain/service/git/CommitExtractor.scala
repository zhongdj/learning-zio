package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.entity.GitCommit
import imadz.team.efficiency.domain.service.GitError
import imadz.team.efficiency.domain.service.git.GitCommands.{cat_file_p, log_full_history_pretty_fuller}
import imadz.team.efficiency.domain.service.git.GitCommitParser.{parseCatFileCommitObject, parseFullerLog}
import imadz.team.efficiency.domain.service.shell._
import zio.IO
import zio.stream._

trait CommitExtractor {

  def extract(dir: String): Stream[GitError, GitCommit] =
    listCommits(dir)
      .mapM(descCommit(dir))

  private def listCommits(dir: String) =
    shellExecMap(log_full_history_pretty_fuller(dir), parseFullerLog)

  private def descCommit(dir: String): GitCommit => IO[GitError, GitCommit] = commit => for {
    detail <- shellExecFold(cat_file_p(dir)(commit.id), parseCatFileCommitObject)
  } yield commit.copy(
    topLevelTreeId = detail.tree,
    parentCommitId = detail.parent
  )

}
