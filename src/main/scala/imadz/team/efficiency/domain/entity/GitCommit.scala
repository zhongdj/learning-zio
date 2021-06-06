package imadz.team.efficiency.domain.entity

import imadz.team.efficiency.domain.DomainEvent

// Read https://git-scm.com/book/en/v2/Git-Internals-Git-Objects
// git cat-file -p $GitID
// Entity
case class GitCommit(id: GitId, topLevelTreeId: GitId, parentCommitId: Option[GitId], author: GitUser, committer: GitUser, message: String)

sealed trait EntryOp {
  def abbr: String

  val map: Map[String, EntryOp] = Map(
    AddFile.abbr -> AddFile,
    CopyFile.abbr -> CopyFile,
    DeleteFile.abbr -> DeleteFile,
    ModifyFile.abbr -> ModifyFile,
    RenameFile.abbr -> RenameFile,
    ChangeType.abbr -> ChangeType,
    UnmergeFile.abbr -> UnmergeFile,
    Unknown.abbr -> Unknown
  )
}

case object AddFile extends EntryOp {
  override def abbr: String = "A"
}
case object CopyFile extends EntryOp {
  override def abbr: String = "C"
}
case object DeleteFile extends EntryOp {
  override def abbr: String = "D"
}
case object ModifyFile extends EntryOp {
  override def abbr: String = "M"
}
case object RenameFile extends EntryOp {
  override def abbr: String = "R"
}
case object ChangeType extends EntryOp {
  override def abbr: String = "T"
}
case object UnmergeFile extends EntryOp {
  override def abbr: String = "U"
}
case object Unknown extends EntryOp {
  override def abbr: String = "X"
}

// Domain Event
case class GitCommitCreated(commit: GitCommit, projectDir: String) extends DomainEvent

sealed trait TreeEntryEvent extends DomainEvent

case class EntryAdded(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, filePath: String) extends TreeEntryEvent
case class EntryDeleted(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, filePath: String) extends TreeEntryEvent
case class EntryMoved(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, srcPath: String, dstPath: String) extends TreeEntryEvent
case class EntryModified(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, filePath: String) extends TreeEntryEvent
case class EntryCopied(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, srcPath: String, dstPath: String) extends TreeEntryEvent
case class EntryTypeChanged(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, filePath: String) extends TreeEntryEvent
case class EntryUnmerged(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, filePath: String) extends TreeEntryEvent
case class EntryUnknown(commitId: GitId, srcMode: String, dstMode: String, srcSha1: String, dstSha1: String, entryOp: EntryOp, filePath: String) extends TreeEntryEvent
