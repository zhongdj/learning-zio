package imadz.team.efficiency.domain.entity.git

case class Contributor(email: String, name: String, id: Int)
case class GitProject(url: String, name: String, id: Int, dir: String)
case class ProjectContributor(projectId: Int, contributorId: Int)

