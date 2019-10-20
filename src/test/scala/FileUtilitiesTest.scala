import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.{Index, IndexEntry}
import sgit.commands.{Add, Init}
import sgit.utilities.FilesUtilities

class FileUtilitiesTest extends FunSuite with DiagrammedAssertions {
  val fileMsg = new File(Init.CurrentDirPath + "/.sgit/MSG_COMMIT")
  val file = new File("TestDir")
  val file1 = new File("TestDir/test1.txt")
  val file2 = new File("TestDir/test2.txt")
  val file4 = new File("TestDir/test4.txt")
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
  test("It should return the files of a directory") {
    assert(
      FilesUtilities
        .filesOfListFiles(List(file))
        .equals(List(file2, file1, file4, file3))
    )

  }

}
