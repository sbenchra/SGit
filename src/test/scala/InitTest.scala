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

}