package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.entity.{AddFile, ChangeType, CopyFile, DeleteFile, EntryAdded, EntryCopied, EntryDeleted, EntryModified, EntryMoved, EntryTypeChanged, EntryUnknown, EntryUnmerged, ModifyFile, RenameFile, TreeEntryEvent, Unknown, UnmergeFile}
import imadz.team.efficiency.domain.service.{GitCommandExecutionError, GitError}
import zio.{IO, ZIO, stream}
import zio.stream.{Stream, UStream}

object GitTreeDiffParser {
  private val treeEntryRegex = """:(.*) (.*) (.*) (.*) (.*)\t(.*)""".r


  def parseTreeDiffContent: UStream[String] => Stream[GitError, TreeEntryEvent] =
    _.map(_.trim).mapM(compileTreeEntry)

  def compileTreeEntry: String => IO[GitError, TreeEntryEvent] = {
    case treeEntryRegex(srcMode, dstMode, srcSha1, dstSha2, op, files) =>
      val event = op match {
        case "A" => EntryAdded("", srcMode, dstMode, srcSha1, dstSha2, AddFile, files)
        case c if c.startsWith("C") => EntryCopied("", srcMode, dstMode, srcSha1, dstSha2, CopyFile, files.trim.split(" ")(0), files.trim.split(" ")(1))
        case "D" => EntryDeleted("", srcMode, dstMode, srcSha1, dstSha2, DeleteFile, files)
        case "M" => EntryModified("", srcMode, dstMode, srcSha1, dstSha2, ModifyFile, files)
        case r if r.startsWith("R") => EntryMoved("", srcMode, dstMode, srcSha1, dstSha2, RenameFile, files.trim.split(" ")(0), files.trim.split(" ")(1))
        case "T" => EntryTypeChanged("", srcMode, dstMode, srcSha1, dstSha2, ChangeType, files)
        case "U" => EntryUnmerged("", srcMode, dstMode, srcSha1, dstSha2, UnmergeFile, files)
        case "X" => EntryUnknown("", srcMode, dstMode, srcSha1, dstSha2, Unknown, files)
      }
      IO.succeed(event)
    case e => IO.fail(GitCommandExecutionError(e))
  }
}
