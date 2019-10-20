import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.Index
import sgit.commands.{Add, Init}

class IndexTest extends FunSuite with DiagrammedAssertions {
  val file3 = new File("TestDir/test4.txt")

  test("The index should contain the path of the staged file") {
    val indexEntry = Index.shaAndPath(file3)
    Init.Init()
    Add.add(List(file3.getAbsolutePath))
    assert(Index.fieldInIndex(indexEntry.path, Index.indexContent))
  }

  test("The index should contain the sha of the staged file") {
    val indexEntry = Index.shaAndPath(file3)
    Init.Init()
    Add.add(List(file3.getAbsolutePath))
    assert(Index.fieldInIndex(indexEntry.sha, Index.indexContent))
  }

}
