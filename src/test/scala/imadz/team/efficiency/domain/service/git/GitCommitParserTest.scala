package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.common.Shells.fromOutput
import imadz.team.efficiency.domain.entity.git
import imadz.team.efficiency.domain.entity.git._
import imadz.team.efficiency.domain.entity.git.GitCommit
import imadz.team.efficiency.domain.service
import imadz.team.efficiency.domain.service.GitCommandExecutionError
import imadz.team.efficiency.domain.service.git.GitCommitParser.{parseCatFileCommitObject, parseFullerLog}
import org.joda.time.DateTime
import zio._
import zio.stream._
import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, _}

object GitCommitParserTest extends DefaultRunnableSpec {
  def spec = suite("Git Commit Parsers")(
    suite("GitCommitLog Parser")(
      testM("recognize commits") {
        // Given
        val logOutput = fromOutput(
          """commit b2a58bc5a4f761da4385ad768b53efaaa8d34b48
            |Author:     Barry Zhong <zhongdj@gmail.com>
            |AuthorDate: 2020-11-28T23:18:07+08:00
            |Commit:     Barry Zhong <zhongdj@gmail.com>
            |CommitDate: 2020-11-28T23:18:07+08:00
            |
            |    fix compile error
            |
            |commit 79e39bf41d2e7ab01baa3e1b4e5c01e01a44e9a8
            |Author:     Barry Zhong <zhongdj@gmail.com>
            |AuthorDate: 2020-10-07T19:41:26+08:00
            |Commit:     Barry Zhong <zhongdj@gmail.com>
            |CommitDate: 2020-10-07T19:41:26+08:00
            |
            |    add interval opts at http req
            |
            |""".stripMargin)
        // When
        val commits: Stream[service.GitError, GitCommit] = parseFullerLog(logOutput)
        for {
          actual <- commits.runCollect
          // Then
        } yield assert(actual) {
          equalTo(Chunk.fromIterable(List(
            git.GitCommit(
              id = "b2a58bc5a4f761da4385ad768b53efaaa8d34b48",
              topLevelTreeId = "", parentCommitId = None,
              author = GitUser(name = "Barry Zhong", email = "zhongdj@gmail.com", timestamp = new DateTime("2020-11-28T23:18:07+08:00")),
              committer = GitUser(name = "Barry Zhong", email = "zhongdj@gmail.com", timestamp = new DateTime("2020-11-28T23:18:07+08:00")),
              message = "fix compile error"
            ), git.GitCommit(
              id = "79e39bf41d2e7ab01baa3e1b4e5c01e01a44e9a8",
              topLevelTreeId = "", parentCommitId = None,
              author = GitUser(name = "Barry Zhong", email = "zhongdj@gmail.com", timestamp = new DateTime("2020-10-07T19:41:26+08:00")),
              committer = GitUser(name = "Barry Zhong", email = "zhongdj@gmail.com", timestamp = new DateTime("2020-10-07T19:41:26+08:00")),
              message = "add interval opts at http req"
            )
          )))
        }
      },
      testM("recognize Git Error") {
        val logOutput = fromOutput("""fatal: not a git repository (or any of the parent directories): .git""")
        val commits = parseFullerLog(logOutput)
        assertM(commits.runCollect.run)(fails(equalTo(
          GitCommandExecutionError("fatal: not a git repository (or any of the parent directories): .git")
        )))
      }
    ),
    suite("GitCommitCatFile Parser")(
      testM("recognize cat-file initial commit without parent commit") {
        // Given
        val commitCatOutput = fromOutput(
          """|tree 39e9a79d63fcac837ea610ceec36f257eea7512d
             |author Barry Zhong <zhongdj@gmail.com> 1622886153 +0800
             |committer Barry Zhong <zhongdj@gmail.com> 1622886153 +0800
             |
             |initial commit""".stripMargin)

        // When
        for {
          actual <- parseCatFileCommitObject(commitCatOutput)
        } yield
          // Then
          assert(actual)(equalTo(
            CatCommitObject(
              tree = "39e9a79d63fcac837ea610ceec36f257eea7512d",
              parent = None,
              authorName = "Barry Zhong",
              authorEmail = "zhongdj@gmail.com",
              timestamp = 1622886153L,
              locale = "+0800"
            )))
      },
      testM("recognize cat-file commit with parent commit") {
        // Given
        val commitCatOutput = fromOutput(
          """tree 76a0b9295b314274da37e27381daa06a13a5cf89
            |parent f958dae046eb9085909980b8fc26f28bd80ec35b
            |author Barry Zhong <zhongdj@gmail.com> 1594455445 +0800
            |committer Barry Zhong <zhongdj@gmail.com> 1594455445 +0800
            |
            |reformat algo stuff
            |
            |""".stripMargin)
        // When
        for {
          actual <- parseCatFileCommitObject(commitCatOutput)
        } yield
          // Then
          assert(actual)(equalTo(
            CatCommitObject(
              tree = "76a0b9295b314274da37e27381daa06a13a5cf89",
              parent = Some("f958dae046eb9085909980b8fc26f28bd80ec35b"),
              authorName = "Barry Zhong",
              authorEmail = "zhongdj@gmail.com",
              timestamp = 1594455445L,
              locale = "+0800"
            )))
      },
      testM("recognize Git Error") {
        // Given
        val commitCatOutput = fromOutput("""fatal: not a git repository (or any of the parent directories): .git""")
        // When
        val willFail: IO[service.GitError, CatCommitObject] = parseCatFileCommitObject(commitCatOutput)
        // Then
        assertM(willFail.run)(fails(
          equalTo(GitCommandExecutionError("fatal: not a git repository (or any of the parent directories): .git")
          )))
      }
    )
  )

}
