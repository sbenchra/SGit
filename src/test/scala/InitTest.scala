import org.scalatest._
import java.io.File
import sgit.commands._
import sgit.utilities._
import sgit._

class InitTest extends FunSuite with DiagrammedAssertions {
  var dir = new File(System.getProperty("user.dir"))
  var repo = Repository

  test("Repository shouldn't initialized") {
    assert(dir.listFiles().map(_.getName()).contains(".sgit") == false)
  }
  /*
  var init = Init
  init.Init()

  Thread.sleep(9000)

  dir = new File(System.getProperty("user.dir"))

  test("Repository is correctly initialized") {
    assert(dir.listFiles().map(_.getName()).contains(".sgit"))
  }

  FilesUtilities.deleteRecursively(new File("./.sgit"))
  */
}