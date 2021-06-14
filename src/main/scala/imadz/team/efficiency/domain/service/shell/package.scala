package imadz.team.efficiency.domain.service

import zio.IO
import zio.stream.{Stream, UStream}

import scala.collection.mutable.ListBuffer
import scala.sys.process._

package object shell {
  def shellExecMap[E, A](command: String, parser: UStream[String] => Stream[E, A]): Stream[E, A] =
    parser.apply(Stream.fromIterable(command.lineStream))

  def shellExecFold[E, A](command: String, parser: UStream[String] => IO[E, A]): IO[E, A] =
    parser.apply(Stream.fromIterable(command.lineStream))

  def shellExecUnit[E](command: String, parser: UStream[String] => IO[E, Unit]): IO[E, Unit] = {
    val errorLogger = new ErrorLogger(command)
    IO.succeed[Int](command ! errorLogger)
      .flatMap[Any, E, Unit] {
        case 0 => IO.succeed(Unit)
        case _ => parser(errorLogger.errStream)
      }
  }

  def shellExecUnit[E](commands: List[String], parser: UStream[String] => IO[E, Unit]): IO[E, Unit] =
    commands.foldLeft[IO[E, Unit]](IO.succeed(Unit)) { (r, c) =>
      r.zipRight(shellExecUnit(c, parser))
    }

  private class ErrorLogger(command: String) extends ProcessLogger {
    private val xs: ListBuffer[String] = ListBuffer(s"command: $command + \n")

    override def out(s: => String): Unit = ()

    override def err(s: => String): Unit = xs.append(s + "\n")

    override def buffer[T](f: => T): T = f

    // does it thread safe?
    def errStream: UStream[String] = UStream.fromIterable(xs)
  }
}
