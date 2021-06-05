package example

import zio.{ExitCode, IO, URIO, ZIO}
import zio.console._

import java.io.IOException
import java.lang.Throwable
import scala.util.Try

object HelloZio extends zio.App {

  val myAppLogic: ZIO[Console, IOException, Unit] =
    for {
      _ <- putStrLn("Hello! What's your name?")
      name <- getStrLn
      _ <- putStrLn(s"Hello, ${name} welcome to ZIO!")
    } yield ()

  def run(args: List[String]): URIO[Console, ExitCode] =
    myAppLogic.exitCode
}

object IntegrationExample extends App {

  import zio._

  val runtime = Runtime.default

  runtime.unsafeRun(Task(println("Hello World!")))
}

object CreatingEffect {

  import zio._
  // Overview
  // R-, E+, A
  // RIO[R, A]   = ZIO[R, Nothing, A]
  // UIO[A]      = ZIO[Any, Nothing, A]
  // URIO[R, A]  = ZIO[R, Nothing, A]
  // Task[A]     = ZIO[Any, Throwable, A]
  // IO[E, A]    = ZIO[Any, E, A]

  // From Success Values
  val s1: UIO[Int] = ZIO.succeed(42)
  val s2: Task[Int] = Task.succeed(42)
  val now: UIO[Long] = ZIO.effectTotal(System.currentTimeMillis())

  // From Failures
  val f1: IO[String, Nothing] = ZIO.fail("Uh oh!")
  val f2: Task[Nothing] = Task.fail(new Exception("Uh oh!"))

  // From Scala Values
  // - Option
  val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
  val zoption2: IO[Option[Nothing], Option[Int]] = zoption.asSome
  val zoption3: IO[String, Int] = zoption.mapError(_ => "It wasn't there!")
  // * Effect E  or Effect A
  // IO[String, Int] => IO[String, Option[Int]]
  val zoption4: IO[String, Option[Int]] = zoption3.asSome
  val zoption5: IO[Option[String], Int] = zoption3.asSomeError
  // * Effect E <=> Effect A
  val zoption6: IO[Option[String], Int] = zoption4.some
  val zoption7: IO[String, Option[Int]] = zoption5.optional

  case class User(id: String, teamId: String)

  case class Team(id: String)

  val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("2"))

  def getUser(id: String): IO[Throwable, Option[User]] = ???

  def getTeam(id: String): IO[Throwable, Team] = ???

  val result: IO[Throwable, Option[(User, Team)]] = {
    for {
      id <- maybeId
      user <- getUser(id).some
      team <- getTeam(user.teamId).asSomeError
    } yield (user, team)
  }.optional

  // - Either
  val zeither: IO[Nothing, String] = ZIO.fromEither(Right("Success!"))
  // - Try
  val ztry: Task[Int] = ZIO.fromTry(Try(42 / 0))
  // - Function
  val zfunction: URIO[Int, Int] = ZIO.fromFunction((i: Int) => i * i)
  // - Future

  import scala.concurrent.Future

  lazy val future = Future.successful("Hello!")
  val zfuture: Task[String] = ZIO.fromFuture { implicit ec => future.map(_ => "Good Bye!") }
  // From Side Effects
  // - Synchronous Side-Effects

  import scala.io.StdIn

  val getStrLn: Task[String] = ZIO.effect(StdIn.readLine())

  def putStrLn(line: String): UIO[Unit] = ZIO.effectTotal(println(line))

  val getStrLn2: IO[IOException, String] = getStrLn.refineOrDie[IOException] { case _ => new IOException() }

  // - Asynchronous Side-Effects
  case class AuthError(code: String)

  object legacy {
    def login(
               onSuccess: User => Unit,
               onFailure: AuthError => Unit
             ): Unit = ???
  }

  val login: IO[AuthError, User] = IO.effectAsync[AuthError, User] { callback =>
    legacy.login(
      user => callback(IO.succeed(user)),
      err => callback(IO.fail(err))
    )
  }
  // Blocking Synchronous Side-Effects

  import zio.blocking._

  val sleeping: RIO[Blocking, Unit] = effectBlocking(Thread.sleep(5L))

  import java.net.ServerSocket

  def accept(l: ServerSocket) = effectBlockingCancelable(l.accept())(UIO.effectTotal(l.close()))

  import scala.io.{Codec, Source}

  def download(url: String): Task[String] = Task.effect(
    Source.fromURL(url)(Codec.UTF8).mkString
  )

  def safeDownload(url: String): ZIO[Blocking, Throwable, String] = zio.blocking.blocking(download(url))
}
