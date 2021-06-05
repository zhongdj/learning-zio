package imadz.team.efficiency.domain.entity

import imadz.team.efficiency.domain.DomainEvent

// Read https://git-scm.com/book/en/v2/Git-Internals-Git-Objects
// git cat-file -p $GitID
// Entity
case class GitCommit(id: GitId, topLevelTreeId: GitId, parentCommitId: Option[GitId], author: GitUser, committer: GitUser, message: String)
// Domain Event
case class GitCommitCreated(commit: GitCommit) extends DomainEvent