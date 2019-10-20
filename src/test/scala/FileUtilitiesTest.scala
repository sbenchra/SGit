import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.commands.{Add, Init}
import sgit.utilities.FilesUtilities

class FileUtilitiesTest extends FunSuite with DiagrammedAssertions {
  val fileMsg = new File(Init.CurrentDirPath + "/.sgit/MSG_COMMIT")
  val file = new File("TestDir")
  val file1 = new File("TestDir/test1.txt")
  val file2 = new File("TestDir/test2.txt")
  val file3 = new File("TestDir/test.txt")

  test("It should return the written message") {

    FilesUtilities
      .writeCommitMessage("hello")
    assert(
      FilesUtilities
        .readFileContent(fileMsg)
        .equals(List("hello"))
    )
  }

}
