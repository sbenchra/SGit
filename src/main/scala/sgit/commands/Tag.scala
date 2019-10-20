package sgit.commands

import java.io.File

import sgit.Repository
import sgit.utilities.FilesUtilities

object Tag {

  //Tag directory path
  def tagsDirPath(): String =
    Repository.get.getAbsolutePath + "/.sgit/refs/tags/"

  //Create tag
  //@param : tagName : String -> tag name
  def createTag(tagName: String): Unit = {
    val file = new File(tagsDirPath + tagName)
    if (tagFiles().contains(tagName)) println("This Tag already exists")
    else {
      Branch.commitWrite(file)
    }

  }
  //Tag files
  //@return : List[String] -> List of tag files
  def tagFiles(): List[String] =
    FilesUtilities
      .filesOfListFiles(List(new File(tagsDirPath())))
      .map(_.getName)
  def main(args: Array[String]): Unit = {

    val file3 = new File("TestDir")
    println(
      FilesUtilities
        .filesOfListFiles(List(file3))
        .map(_.getAbsolutePath)
    )

    // Diff.diff()
  }

}
