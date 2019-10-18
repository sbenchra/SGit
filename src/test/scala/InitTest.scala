import org.scalatest._
import java.io.File
import sgit.commands._
import sgit.utilities._
import sgit._

class InitTest extends FunSuite with DiagrammedAssertions {
  val dir = new File(System.getProperty("user.dir"))
  val repo = Repository


  test("Repository should be initialized")
  {
    Init.Init()
    assert(dir.listFiles().map(_.getName).contains(".sgit"))
  }
  test("Should check if the repository is already initialized")
  {
    Init.Init()
    assert(Repository.isInitialized(dir.getAbsolutePath))
  }

}