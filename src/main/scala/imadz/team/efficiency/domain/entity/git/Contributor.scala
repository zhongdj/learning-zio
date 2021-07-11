package imadz.team.efficiency.domain.entity.git

case class Contributor(email: String, name: String, id: Int)

case class GitProject(url: String, branch: String, id: Int, parentDir: String) {
  private val nameRegex = """.*/(.*?)(?:\.git)?""".r
  lazy val name: String = url match {
    case nameRegex(projectName) => projectName
  }
  def dir: String = s"$parentDir/$name"
}

case class ProjectContributor(projectId: Int, contributorId: Int)

