package imadz.team.efficiency.application.read.model

import imadz.team.efficiency.domain.entity.{GitId, Project}

import scala.util.matching.Regex

case class CommitMessagePattern(regex: Regex)
case class CommitMessageViolation(id: Int, project: Project, commitId: GitId, violatedMessage: String)
