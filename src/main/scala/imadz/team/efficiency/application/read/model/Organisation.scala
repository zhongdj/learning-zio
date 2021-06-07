package imadz.team.efficiency.application.read.model

case class Organisation(id: Int, name: String, typeName: String, members: Map[Role, OrganisationMember])

case class OrganisationMember(id: Int, name: String, email: String)

case class Role(id: Int, name: String)

case class HierarchyRelation(fromOrganisation: Organisation, toOrganisation: Organisation, name: String)

object OrganisationTypes {
  val project = "Project"
  val team = "Team"
}

object RoleNames {
  val leader = "Leader"
  val manager = "Manager"
  val contributor = "Contributor"
}

object RelationNames {
  val partial = "Partial" // a smaller project is part of a bigger project
  val reports = "Reports" // a contributor(member) reports to a leader(supervisor)
}