package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.entity.{GitCommit, GitId, GitUser}
import imadz.team.efficiency.domain.service.{GitCommandExecutionError, GitError}
import org.joda.time.DateTime
import zio.stream._
import zio.{Chunk, IO}

object GitCommitParser {

  def parseCatFileCommitObject(lineStream: UStream[String]): IO[GitError, CatCommitObject] = {
    import CatFileCommitParser._
    lineStream.fold("")((x, y) => s"$x\n$y")
      .map(_.trim)
      .flatMap(compileCatFileCommit)
  }

  def parseFullerLog(logOutput: UStream[String]): Stream[GitError, GitCommit] = {
    import LogCommitParser._
    logOutput
      .aggregate[Any, Nothing, ParserState](ZTransducer.foldLeft[String, ParserState](initialState)((xs, x) => reduce(xs, x)))
      .mapConcat[Chunk[String]](pair => List(pair._1, pair._2))
      .mapM(toGitCommit)
  }

}

package object CatFileCommitParser {
  val compileCatFileCommit: String => IO[GitError, CatCommitObject] = {
    case catFileCommitRegex(id, commitId, authorName, authorEmail, timestamp, locale, _) => IO.succeed(
      CatCommitObject(id.trim, Some(commitId), authorName, authorEmail, timestamp.toLong, locale)
    )
    case catFileInitCommitRegex(id, authorName, authorEmail, timestamp, locale, _) => IO.succeed(
      CatCommitObject(id.trim, None, authorName, authorEmail, timestamp.toLong, locale)
    )
    case err => IO.fail(GitCommandExecutionError(err.trim))
  }

  private val catFileCommitRegex = """(?s)^tree (.*)\nparent (.*)\nauthor (.*) <(.*)> (.*) (.*)\ncommitter .*\n\n(.*)$""".r
  private val catFileInitCommitRegex = """(?s)^tree (.*)\nauthor (.*) <(.*)> (.*) (.*)\ncommitter .*\n\n(.*)$""".r
}

package object LogCommitParser {
  type ParserState = (Chunk[String], Chunk[String])
  val initialState: ParserState = (Chunk.empty, Chunk.empty)

  def reduce(parserState: ParserState, line: String): ParserState = parserState match {
    case (chunkAcc, chunkX) if line.startsWith("commit") => (chunkAcc ++ chunkX, Chunk(line))
    case (chunkAcc, chunkX) if chunkAcc.isEmpty && line.startsWith("fatal") => (Chunk(line), chunkX)
    case (chunkAcc, chunkX) => (chunkAcc, chunkX + line)
  }

  val toGitCommit: Chunk[String] => IO[GitError, GitCommit] = xs =>
    compileGitCommit(xs.fold("")((x, y) => s"$x\n$y").trim)

  private def compileGitCommit: String => IO[GitError, GitCommit] = {
    case commitRegex(id, autherName, autherEmail, authorDate, committerName, committerEmail, commitDate, message) => IO.succeed(
      GitCommit(
        id = id.trim,
        topLevelTreeId = "",
        parentCommitId = None,
        author = GitUser(autherName, autherEmail, new DateTime(authorDate.trim)),
        committer = GitUser(committerName, committerEmail, new DateTime(commitDate.trim)),
        message = message.trim
      ))
    case error => IO.fail(GitCommandExecutionError(error))
  }

  private val commitRegex = """(?s)^commit (.*)\nAuthor:\s+(.*) <(.*)>\nAuthorDate: (.*)\nCommit:\s+(.*) <(.*)>\nCommitDate: (.*)\n(.*)$""".r
}

case class CatCommitObject(tree: GitId, parent: Option[GitId], authorName: String, authorEmail: String, timestamp: Long, locale: String)