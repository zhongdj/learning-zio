package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.common.Shells.fromOutput
import imadz.team.efficiency.domain.entity._
import imadz.team.efficiency.domain.entity.git.{AddFile, CopyFile, DeleteFile, EntryAdded, EntryCopied, EntryDeleted, EntryModified, EntryMoved, EntryUnmerged, ModifyFile, RenameFile, UnmergeFile}
import imadz.team.efficiency.domain.service.GitCommandExecutionError
import imadz.team.efficiency.domain.service.git.GitTreeDiffParser.parseTreeDiffContent
import zio.Chunk
import zio.test.Assertion.{equalTo, fails}
import zio.test._

object GitDiffParserTest extends DefaultRunnableSpec {

  /*
 in-place edit  :100644 100644 bcd1234 0123456 M file0
 copy-edit      :100644 100644 abcd123 1234567 C68 file1 file2
 rename-edit    :100644 100644 abcd123 1234567 R86 file1 file3
 create         :000000 100644 0000000 1234567 A file4
 delete         :100644 000000 1234567 0000000 D file5
 unmerged       :000000 000000 0000000 0000000 U file6

 Possible status letters are:

 A: addition of a file
 C: copy of a file into a new one
 D: deletion of a file
 M: modification of the contents or mode of a file
 R: renaming of a file
 T: change in the type of the file
 U: file is unmerged (you must complete the merge before it can be committed)
 X: "unknown" change type (most probably a bug, please report it)
 */

  override def spec = suite("Git diff-tree Parser")(

    testM("all tree entry op type should be compiled") {
      // Given
      val diffTreeOutput = fromOutput(
        """:100644 100644 bcd1234 0123456 M	file0
          |:100644 100644 abcd123 1234567 C68	file1 file2
          |:100644 100644 abcd123 1234567 R86	file1 file3
          |:000000 100644 0000000 1234567 A	file4
          |:100644 000000 1234567 0000000 D	file5
          |:000000 000000 0000000 0000000 U	file6""".stripMargin)

      // When
      for {
        xs <- parseTreeDiffContent(diffTreeOutput).runCollect
      } yield {
        // Then
        assert(xs)(equalTo(Chunk(
          EntryModified("", "100644", "100644", "bcd1234", "0123456", ModifyFile, "file0"),
          EntryCopied("", "100644", "100644", "abcd123", "1234567", CopyFile, "file1", "file2"),
          EntryMoved("", "100644", "100644", "abcd123", "1234567", RenameFile, "file1", "file3"),
          EntryAdded("", "000000", "100644", "0000000", "1234567", AddFile, "file4"),
          EntryDeleted("", "100644", "000000", "1234567", "0000000", DeleteFile, "file5"),
          EntryUnmerged("", "000000", "000000", "0000000", "0000000", UnmergeFile, "file6")
        )))
      }
    },

    testM("git command fatal should be compiled") {
      // Given
      val diffTreeOutput = fromOutput("fatal: not a git repository (or any of the parent directories): .git")

      // When
      val err = parseTreeDiffContent(diffTreeOutput).runCollect.run

      // Then
      assertM(err)(fails(equalTo(
        GitCommandExecutionError("fatal: not a git repository (or any of the parent directories): .git")
      )))
    }
  )
}
