package imadz.team.efficiency.domain.entity

import org.joda.time.DateTime

package object git {
  type GitId = String
  type FileMode = String

  // Example: author Scott Chacon <schacon@gmail.com> 2021-05-28T22:28:09-0700
  case class GitUser(name: String, email: String, timestamp: DateTime)
}
