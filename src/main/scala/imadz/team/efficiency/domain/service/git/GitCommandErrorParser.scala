package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.service.{GitCommandExecutionError, GitError}
import zio.IO
import zio.stream.UStream

trait GitCommandErrorParser {

  def errorParser: UStream[String] => IO[GitError, Unit] = errStream => {
    errStream.fold("")(concat)
      .flatMap(msg => IO.fail(GitCommandExecutionError(msg))).unit
  }
  private val concat: (String, String) => String = (x, y) => x + y

}
