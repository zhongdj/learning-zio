package imadz.team.efficiency.application.read.model

import imadz.team.efficiency.domain.entity.git.{Contributor, GitProject}
import org.joda.time.DateTime

case class ContributorByPeriod(contributor: Contributor, crossProjectPeriods: List[CrossProjectPeriod])

case class CrossProjectPeriod(from: DateTime, to: DateTime, projects: List[GitProject])
