package imadz.team.efficiency.domain

object Errors {

  trait DomainError {
    def msg: String
  }

  case class GitCommitExtractError(msg: String) extends DomainError
  case class GitDiffTreeExtractError(msg: String) extends DomainError
  case class FileOrDirectoryAccessDenied(msg: String) extends DomainError
  case class UnwantedGitRepository(msg: String) extends DomainError
  case class ResetGitRepositoryError(msg: String) extends DomainError
  case class CloneGitRepositoryError(msg: String) extends DomainError
}
