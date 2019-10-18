import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.{Index, IndexEntry}

class IndexTest extends FunSuite with DiagrammedAssertions {

  val file=new File("soufiane/test.txt")
  test("Should test if it correctly creates index of a directory"){

    assert(Index.workingDirIndex(List(file)).head==IndexEntry("/home/benchraa/SGit/soufiane/test.txt","29b40318f1b2a1c8b8a59dde07539ae6a05b9156")
    )
  }

}
