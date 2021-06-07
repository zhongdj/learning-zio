package imadz.team.efficiency.application.read.model

import imadz.team.efficiency.domain.entity.{Contributor, Project}
import org.joda.time.DateTime

case class ContributorByPeriod(contributor: Contributor, crossProjectPeriods: List[CrossProjectPeriod])

case class CrossProjectPeriod(from: DateTime, to: DateTime, projects: List[Project])
