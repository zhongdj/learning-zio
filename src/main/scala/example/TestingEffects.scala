package example

import zio._
import zio.console._

object TestingEffects {

  // Environments
  val e: URIO[Int, Int] = for {
    env <- ZIO.environment[Int]
  } yield env

  for {
    env <- e
    _ <- putStrLn(s"The value of the environment is: $env")
  } yield env

  final case class Config(server: String, port: Int)

  val configString: URIO[Config, String] =
    for {
      server <- ZIO.access[Config](_.server)
      port <- ZIO.access[Config](_.port)
    } yield s"Server: $server, port: $port"

  trait DatabaseOps {
    def getTableNames: Task[List[String]]

    def getColumnNames(table: String): Task[List[String]]
  }

  val tableAndColumns: ZIO[DatabaseOps, Throwable, (List[String], List[String])] =
    for {
      tables <- ZIO.accessM[DatabaseOps](_.getTableNames)
      columns <- ZIO.accessM[DatabaseOps](_.getColumnNames("user_table"))
    } yield (tables, columns)
  // - Providing Environments
  val square: URIO[Int, Int] =
    for {
      env <- ZIO.environment[Int]
    } yield env * env

  val result: UIO[Int] = square.provide(42)

  // Environmental Effects
  // - Define the Service
  case class UserID(id: String)

  case class UserProfile(id: String, name: String)

  object Database {
    trait Service {
      def lookup(id: UserID): Task[UserProfile]

      def update(id: UserID, profile: UserProfile): Task[Unit]
    }
  }

  trait Database {
    def database: Database.Service
  }

  // - Provide Helpers
  object db {
    def lookup(id: UserID): RIO[Database, UserProfile] = ZIO.accessM[Database](_.database.lookup(id))

    def update(id: UserID, profile: UserProfile): RIO[Database, Unit] = ZIO.accessM[Database](_.database.update(id, profile))
  }

  // - Use the Service
  val lookedupProfile: RIO[Database, UserProfile] =
    for {
      profile <- db.lookup(UserID(""))
    } yield profile

  // - Implement Live Service
  trait DatabaseLive extends Database {
    def database: Database.Service =
      new Database.Service {
        override def lookup(id: UserID): Task[UserProfile] = ???

        override def update(id: UserID, profile: UserProfile): Task[Unit] = ???
      }
  }

  object DatabaseLive extends DatabaseLive
  // - Run the Database Effect

  def main: RIO[Database, Unit] = ???

  def main2: Task[Unit] = main.provide(DatabaseLive)

  // - Implement Test Service
  class TestService extends Database.Service {
    private var map: Map[UserID, UserProfile] = Map()

    def setTestData(map0: Map[UserID, UserProfile]): Task[Unit] = Task {
      map = map0
    }

    def getTestData: Task[Map[UserID, UserProfile]] = Task {
      map
    }

    override def lookup(id: UserID): Task[UserProfile] = Task(map(id))

    override def update(id: UserID, profile: UserProfile): Task[Unit] = Task(map += id -> profile)
  }
  trait TestDatabase  extends Database {
    val database: TestService = new TestService
  }
  object TestDatabase extends TestDatabase
  // - Test Database Code
  def code: RIO[Database, Unit] = ???
  def code2: Task[Unit] = code.provide(TestDatabase)

}
