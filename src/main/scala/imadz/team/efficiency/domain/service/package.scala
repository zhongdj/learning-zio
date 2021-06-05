package imadz.team.efficiency.domain

package object service {
  trait GitError {
    def message: String
  }

  case class GitCommandExecutionError(message: String) extends GitError
  case class CommitLogIncompletedError(message: String) extends GitError
  object ServiceError {

  }
}
