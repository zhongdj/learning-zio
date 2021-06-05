package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.entity.{GitCommit, GitId}
import imadz.team.efficiency.domain.service.{GitCommandExecutionError, GitError}
import zio.IO
import zio.stream._

object GitCommitParser {

  private val catFileCommitRegex = """(?s)^tree (.*)\nparent (.*)\nauthor (.*) <(.*)> (.*) (.*)\ncommitter .*\n\n(.*)$""".r
  private val catFileInitCommitRegex = """(?s)^tree (.*)\nauthor (.*) <(.*)> (.*) (.*)\ncommitter .*\n\n(.*)$""".r

  private val compileCatFileCommit: String => IO[GitError, CatCommitObject] = {
    case catFileCommitRegex(id, commitId, authorName, authorEmail, timestamp, locale, _) => IO.succeed(
      CatCommitObject(id.trim, Some(commitId), authorName, authorEmail, timestamp.toLong, locale)
    )
    case catFileInitCommitRegex(id, authorName, authorEmail, timestamp, locale, _) => IO.succeed(
      CatCommitObject(id.trim, None, authorName, authorEmail, timestamp.toLong, locale)
    )
    case err => IO.fail(GitCommandExecutionError(err.trim))
  }

  def parseCatFileCommitObject(lineStream: UStream[String]): IO[GitError, CatCommitObject] = {
    lineStream.fold("")((x, y) => s"$x\n$y")
      .map(_.trim)
      .flatMap(compileCatFileCommit)
  }

  def parseFullerLog(logOutput: UStream[String]): Stream[GitError, GitCommit] = ???

}

case class CatCommitObject(tree: GitId, parent: Option[GitId], authorName: String, authorEmail: String, timestamp: Long, locale: String)