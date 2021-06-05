package example

import zio._

object BasicConcurrency {
  // Fibers
  // - The Fiber Data Type
  val f: Fiber[String, Int] = ???

  // - Forking
  def fib(n: Long): UIO[Long] =
    if (n <= 1) UIO.succeed(1)
    else fib(n - 1).zipWith(fib(n - 2))(_ + _)

  val fib100Fiber: UIO[Fiber[Nothing, Long]] = for {
    fiber <- fib(100).fork
  } yield fiber

  // - Joining
  val j: UIO[String] = for {
    fiber <- ZIO.succeed("Hello").fork
    message <- fiber.join
  } yield message

  // - Awaiting
  val a: UIO[Exit[Nothing, String]] = for {
    fiber <- IO.succeed("Hello").fork
    exit <- fiber.await
  } yield exit

  // - Interrupting
  val i: UIO[Unit] = for {
    fiber <- IO.succeed("Hi!").forever.fork
    _ <- fiber.interrupt
  } yield ()

  // - Composing
  val z: UIO[(String, String)] = for {
    fiber1 <- IO.succeed("Hi").fork
    fiber2 <- IO.succeed("Bye").fork
    tuple <- fiber1.zip(fiber2).join
  } yield tuple

  val z2: UIO[String] = for {
    fiber1 <- IO.fail("Hi").fork
    fiber2 <- IO.succeed("Bye").fork
    fiber = fiber1.orElse(fiber2)
    tuple <- fiber.join
  } yield tuple
  // Parallelism
  // Racing
  val w: UIO[String] = for {
    winner <- IO.succeed("Hello").race(IO.succeed("Goodbye"))
  } yield winner

  private val left: UIO[Either[String, String]] = IO.fail("Hello").either
  private val right: UIO[Either[String, String]] = IO.fail("Goodbye").either
  val mightFail: UIO[Either[String, String]] = left race right
  // Timeout
  import zio.duration._
  IO.succeed("Hello").timeout(10 seconds)
}
