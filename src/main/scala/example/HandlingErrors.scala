package example

import zio._

import java.io.IOException

object HandlingErrors {
  // Either
  // - IO[String, A] => IO [Nothing, Either[E, A]] => IO[E, A]
  // - either: IO[String, A] => UIO[Either, A]
  // - absolve: UIO[Either, A] => IO[E, A]
  val e1: IO[String, Int] = IO.fail("Stupid Error")
  val e2: UIO[Either[String, Int]] = e1.either
  val e3: IO[String, Int] = e2.absolve

  def sqrt(io: UIO[Double]): IO[String, Double] =
    ZIO.absolve(io map pureSqrt)

  private def pureSqrt(i: Double) = {
    if (i < 0) Left("Cannot be negative")
    else Right(Math.sqrt(i))
  }

  // Catching All Errors
  def openFile(path: String): IO[IOException, Array[Byte]] = ???

  openFile("primary.json")
    .catchAll(_ => openFile("default.json"))
  // Catching Some Errors
  openFile("primary.json").catchSome {
    case _ => openFile("default.json")
  }
  // Fallbacks
  openFile("primary.json")
    .orElse(openFile("default.json"))
  // Folding
  // - non-effectfully
  lazy val defaultData: Array[Byte] = Array(0, 1, 2, 3)
  openFile("primary.json").fold(
    _ => defaultData,
    data => data
  )
  // - effectfully
  openFile("primary.json").foldM(
    _ => openFile("default.json"),
    data => ZIO.succeed(data)
  )

  sealed trait Content

  case class TextContent(text: String) extends Content

  case class NoContent(error: Throwable) extends Content

  def readUrls(file: String): IO[IOException, String] = ???

  def fetchContent(url: String): UIO[TextContent] = ???

  def urls: UIO[Content] = readUrls("urls.json").foldM(
    err => ZIO.succeed(NoContent(err)),
    success => fetchContent(success)
  )
  // Retrying

  import zio.clock._

  val retriedOpenFile: ZIO[Clock, IOException, Array[Byte]] =
    openFile("primary.json").retry(Schedule.recurs(5))

//  val retriedOpenFile2: ZIO[Clock, Nothing, Array[Byte]] =
//    openFile("primary.json").retryOrElse(
//      Schedule.recurs(5),
//      (x, y: Array[Byte]) => ZIO.succeed(defaultData)
//    )

}
