package imadz.team.efficiency.domain

object Errors {

  trait DomainError
  case class GitCommitExtractError(msg: String) extends DomainError
  case class GitDiffTreeExtractError(msg: String) extends DomainError

}
