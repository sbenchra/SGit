import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.{Index, IndexEntry}

class IndexTest extends FunSuite with DiagrammedAssertions {

  val file=new File("soufiane/test.txt")
  test("Should test if it correctly creates index of a directory"){

    assert(Index.workingDirIndex(List(file)).head==IndexEntry("/home/benchraa/SGit/soufiane/test.txt","7fb619752a1dc9aaf830ea4bbce341102e66028b")
    )
  }

}
