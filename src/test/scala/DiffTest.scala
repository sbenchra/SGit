import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.commands.Diff

class DiffTest extends FunSuite with DiagrammedAssertions  {

  val file=new File("soufiane/text.txt")
  test("should compare two list of lines"){
    val l1=List("This is a test","of sgit file")
    val l2=List("I changed this line for the test","of sgit file")
    assert(   Diff.compare(l1,l2)==List("++I changed this line for the test","--This is a test"))
  }

  test("should return a map of file and its content"){
    assert(Diff.dirFilesAndContent(List(file))==Map(file.getPath->List("This is a test")))
  }

}
