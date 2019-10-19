import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.Index
import sgit.commands.{Add, Init}
import sgit.utilities.FilesUtilities

class AddTest extends FunSuite with DiagrammedAssertions {

  val dir = new File(System.getProperty("user.dir"))
  val file = new File(dir + "/soufiane/test.txt")
  test("The index should contain the file sha1 and path of the file") {
    Init.Init()
    val listFile = List(file.getAbsolutePath)
    Add.add(listFile)
    val indexContent = Index.indexContent
    val indexEntry = Index.shaAndPath(file)
    assert(Index.containsIndexEntry(indexEntry, indexContent))
  }
  test("the index shouldn't contain the sha of a modified file ") {
    Init.Init()
    Add.add(List(file.getAbsolutePath))
    FilesUtilities.writeInFile(file, List("Test text"))
    assert(!Index.fieldInIndex(Index.shaAndPath(file).sha, Index.indexContent))
  }

}
