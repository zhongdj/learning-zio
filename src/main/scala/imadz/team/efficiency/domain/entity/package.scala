package imadz.team.efficiency.domain

import org.joda.time.DateTime

package object entity {
  type GitId = String
  type FileMode = String

  // Example: author Scott Chacon <schacon@gmail.com> 2021-05-28T22:28:09-0700
  case class GitUser(name: String, email: String, timestamp: DateTime)
}
