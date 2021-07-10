package imadz.team.efficiency.application.read.model

import imadz.team.efficiency.domain.entity.git.{GitId, GitProject}

import scala.util.matching.Regex

case class CommitMessagePattern(regex: Regex)
case class CommitMessageViolation(id: Int, project: GitProject, commitId: GitId, violatedMessage: String)
