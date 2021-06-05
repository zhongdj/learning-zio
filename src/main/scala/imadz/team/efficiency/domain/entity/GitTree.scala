package imadz.team.efficiency.domain.entity

// Entity
trait GitTreeEntry {
  def id: GitId
  def name: String
  def mode: FileMode
  def parent: GitId
  def blobObjects: List[GitTreeEntry]
}
case class GitFileEntry(name: String, mode: FileMode, id: GitId, parent: GitId) extends GitTreeEntry {
  override def blobObjects: List[GitTreeEntry] = Nil
}
case class GitTree(name: String, id: GitId, mode: FileMode, blobObjects: List[GitTreeEntry], parent: GitId) extends GitTreeEntry

// Domain Events
case class GitTreeEntryAdded(entry: GitTreeEntry)
case class GitTreeEntryDeleted(entry: GitTreeEntry)
case class GitTreeEntryMoved(entry: GitTreeEntry, fromParent: GitId, toParent: GitId)
