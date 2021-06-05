package example

import zio._
import zio.console._

import java.io.IOException

object MyApp2 extends zio.App {

  val myAppLogic: ZIO[Console, IOException, Unit] = for {
    _ <- putStrLn("Please Input Your Name:")
    name <- getStrLn
    _ <- putStrLn(s"Hello, ${name}, welcome to zio")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    myAppLogic.exitCode
}

object RunningEffects extends scala.App {

  val runtime = Runtime.default
  runtime.unsafeRun(putStrLn("Hello World"))

}
