package example

import zio._

import java.io.IOException

object ZioBasicOperations {

  // Map
  // - Map Success
  val succeed: UIO[Int] = ZIO.succeed(2).map(_ * 2)
  // - Map Error
  val failure: IO[Exception, Unit] = IO.fail("No no!").mapError(new Exception(_))
  // Chaining -> Sequential
  // - FlatMap

  import zio.console._
  val sequenced: ZIO[Console, IOException, Unit] = putStrLn("Please input your name:")
    .flatMap(_ => getStrLn)
    .flatMap(line => putStrLn(s"Welcome to zio world, ${line}!"))

  // For Comprehension
  val program: ZIO[Console, IOException, Unit] = for {
    _    <- putStrLn("Please Input Your Name:")
    name <- getStrLn
    _    <- putStrLn(s"Welcome to zio world, ${name}!")
  } yield ()

  // Zip
  // - zip
  val zipped: UIO[(String, Int)] = ZIO.succeed("4").zip(ZIO.succeed(2))
  // - zipLeft | zipRight
  val zipRight1: ZIO[Console, IOException, String] = putStrLn("What's your name?").zipRight(getStrLn)
  val zipRight2: ZIO[Console, IOException, String] = putStrLn("What's your name?") *> (getStrLn)
}
