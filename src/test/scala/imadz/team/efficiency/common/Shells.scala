package imadz.team.efficiency.common

import zio.stream.ZStream.fromIterable

object Shells {
  def fromOutput(catFileOutput: String) = {
    fromIterable(catFileOutput.split("""\n"""))
  }

}
