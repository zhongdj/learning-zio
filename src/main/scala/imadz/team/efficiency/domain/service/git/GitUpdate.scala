package imadz.team.efficiency.domain.service.git

import imadz.team.efficiency.domain.service.GitError
import imadz.team.efficiency.domain.service.git.GitCommands.git_reset_hard_and_update
import imadz.team.efficiency.domain.service.shell.shellExecUnit
import zio.IO

trait GitUpdate extends GitCommandErrorParser {

  def updateHard(workdir: String, branch: String): IO[GitError, Unit] =
    shellExecUnit(git_reset_hard_and_update(workdir, branch), errorParser)

}
