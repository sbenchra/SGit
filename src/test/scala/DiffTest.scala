import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.commands.Diff

class DiffTest extends FunSuite with DiagrammedAssertions {

  val file1 = new File("TestDir/test1.txt")
  test("should compare two list of lines") {
    val l1 = List("This is a test", "of sgit file")
    val l2 = List("I changed this line for the test", "of sgit file")
    assert(
      Diff.compare(l1, l2) == List(
        "++I changed this line for the test",
        "--This is a test"
      )
    )
  }

  test("should return a map of file and its content") {
    assert(
      Diff.dirFilesAndContent(List(file1)) == Map(
        file1.getAbsolutePath -> List("TestBis")
      )
    )
  }

  test("should return the difference between a new and old file content") {
    assert(
      Diff
        .compareMaps(
          Map("Test" -> List("This is test")),
          Map("Test" -> List("This is test 2"))
        )
        .equals(Map("Test" -> List("++This is test 2", "--This is test")))
    )
  }

  test("should return the content of a file and the file in a map") {
    assert(
      Diff
        .dirFilesAndContent(List(file1))
        .equals(Map(file1.getAbsolutePath -> List("TestBis")))
    )

  }

}
