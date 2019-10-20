import java.io.File

import org.scalatest.{DiagrammedAssertions, FunSuite}
import sgit.Index
import sgit.commands.{Add, Commit, Init}
import sgit.utilities.FilesUtilities

class CommitTest extends FunSuite with DiagrammedAssertions {

  val listFiles = List("TestDir/test2.txt")
  val msg = "Test commit"

  test("it should return the split the path by \\") {
    val listPath = List("dir1/dir2/file")
    val splitedList = Commit.fragmentAllPaths(listPath)
    assert(splitedList == List(List("dir1", "dir2", "file")))
  }

  test("it should check if the file is staged") {
    Init.Init()
    Add.add(listFiles)
    Commit.commit(msg)
    assert(
      Commit
        .allFileAreStaged(listFiles.map(x => new File(x)), Index.indexContent)
    )
  }

  test("it shouldn't commit because the change was not staged") {
    Init.Init()
    Add.add(listFiles)
    val file = new File(listFiles.head)
    FilesUtilities.writeInFile(file, List("Test text"))
    Commit.commit(msg)
    assert(!Index.fieldInIndex(Index.shaAndPath(file).sha, Index.indexContent))
  }
  test("it should get the path in the index") {
    assert(
      Commit
        .getPath(Index.indexContent, "test.txt")
        .split("/")
        .last
        .equals("test.txt")
    )
  }
  test("It should give the last element and its parent") {

    assert(
      Commit
        .lastsAndParents(List(List("A", "B", "C", "D"), List("C", "D", "F")))
        .equals(List(List("C", "D"), List("D", "F")))
    )
  }
  test(" It should give the children of a given parent") {
    assert(
      Commit