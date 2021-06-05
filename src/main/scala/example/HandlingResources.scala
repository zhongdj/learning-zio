package example

import zio._

import java.io.{File, IOException}

object HandlingResources {

  // Finalizing
  val finalizer: UIO[Unit] = UIO.effectTotal(println("Finalizing!"))
  val finalized: IO[String, Unit] = IO.fail("Failed!").ensuring(finalizer)

  // Bracket
  def openFile(path: String): IO[IOException, File] = ???

  def closeFile(file: File): UIO[Unit] = ???

  val decodeData: File => IO[IOException, List[String]] = ???
  val groupData: List[String] => Map[String, String] = ???

  def processFile(file: File): UIO[Unit] = ???

  def processFile2(file: File): IO[IOException, Unit] = for {
    data <- decodeData(file)
    grouped <- ZIO.succeed(groupData(data))
  } yield println(grouped.mkString)

  openFile("").bracket(closeFile)(processFile2)
}
