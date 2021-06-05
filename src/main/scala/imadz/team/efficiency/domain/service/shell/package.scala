package imadz.team.efficiency.domain.service

import zio.IO
import zio.stream.{Stream, UStream}
import scala.sys.process._

package object shell {
  def shellExecMap[E, A](command: String, parser: UStream[String] => Stream[E, A]): Stream[E, A] =
    parser.apply(Stream.fromIterable(command.lineStream))

  def shellExecFold[E, A](command: String, parser: UStream[String] => IO[E, A]): IO[E, A] =
    parser.apply(Stream.fromIterable(command.lineStream))

}
