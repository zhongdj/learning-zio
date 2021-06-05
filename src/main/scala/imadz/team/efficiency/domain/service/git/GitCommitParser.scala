package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.entity.{GitCommit, GitId}
import imadz.team.efficiency.domain.service.GitError
import zio.IO
import zio.stream._

object GitCommitParser {
/*
tree 76a0b9295b314274da37e27381daa06a13a5cf89
parent f958dae046eb9085909980b8fc26f28bd80ec35b
author Barry Zhong <zhongdj@gmail.com> 1594455445 +0800
committer Barry Zhong <zhongdj@gmail.com> 1594455445 +0800

reformat algo stuff
*/
  def parseCatFileCommitObject(lineStream: UStream[String]): IO[GitError, CatCommitObject] = ???
/*
commit b2a58bc5a4f761da4385ad768b53efaaa8d34b48
Author:     Barry Zhong <zhongdj@gmail.com>
AuthorDate: 2021-05-28T23:18:07+08:00
Commit:     Barry Zhong <zhongdj@gmail.com>
CommitDate: Fri May 28 23:54:45 2021 +0800

    fix compile error

commit 79e39bf41d2e7ab01baa3e1b4e5c01e01a44e9a8
Author:     Barry Zhong <zhongdj@gmail.com>
AuthorDate: 2021-05-28T23:18:07+08:00
Commit:     Barry Zhong <zhongdj@gmail.com>
CommitDate: Fri May 28 23:18:07 2021 +0800

    add interval opts at http req
*/
  def parseFullerLog(logOutput: UStream[String]): Stream[GitError, GitCommit] = ???

}

case class CatCommitObject(tree: GitId, parent: Option[GitId], authorName: String, authorEmail: String, timestamp: Long, locale: String)

object Demo extends App {
  println("""commit b2a58bc5a4f761da4385ad768b53efaaa8d34b48
            |Author:     Barry Zhong <zhongdj@gmail.com>
            |AuthorDate: 2021-05-28T23:18:07+08:00
            |Commit:     Barry Zhong <zhongdj@gmail.com>
            |CommitDate: Fri May 28 23:54:45 2021 +0800""".stripMargin.split("""\n""").length)
}