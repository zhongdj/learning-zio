package imadz.team.efficiency.domain.entity.mgt

import imadz.team.efficiency.infrastructure.controller.GitProject

case class AnalysisProject(gitProjects: List[GitProject], options: AnalysisProjectOptions, basedir: String)

final case class AnalysisProjectOptions(interval: Int, repeatedCycle: Option[Int] = None)