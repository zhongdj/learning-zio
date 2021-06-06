package imadz.team.efficiency.domain.service.git

object GitDiffParserTest {

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
  """:100644 100644 bcd1234 0123456 M file0
    |:100644 100644 abcd123 1234567 C68 file1 file2
    |:100644 100644 abcd123 1234567 R86 file1 file3
    |:000000 100644 0000000 1234567 A file4
    |:100644 000000 1234567 0000000 D file5
    |:000000 000000 0000000 0000000 U file6""".stripMargin

}
