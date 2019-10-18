import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.{Index, IndexEntry}

class IndexTest extends FunSuite with DiagrammedAssertions {

  val file=new File("soufiane/test.txt")
  test("Should test if it correctly creates index of a directory"){

    assert(Index.workingDirIndex(List(file)).head==IndexEntry("/home/benchraa/SGit/soufiane/test.txt","20942d061369ab5d8fa8f67aad2bfe23d4dc57a8")
    )
  }

}
