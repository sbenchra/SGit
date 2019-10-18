package sgit.commands

import java.io.File

import sgit.Repository
import sgit.utilities.FilesUtilities

object Tag {

  def tagsDirPath(): String =Repository.get.getAbsolutePath+"/.sgit/refs/tags/"

  def tagFiles(): List[String] =FilesUtilities.filesOfListFiles(List(new File(tagsDirPath()))).map(_.getName)

  def createTag(tagName:String):Unit={

      val file = new File(tagsDirPath+tagName)
      if(tagFiles().contains(tagName)) println("This Tag already exists")
      else {
        Branch.commitWrite(file)
      }

    }

  def main(args: Array[String]): Unit = {
    Init.Init()
   Add.add(List("soufiane"))
   Commit.commit("soufiane")
    Log.logStat()


  }


}
