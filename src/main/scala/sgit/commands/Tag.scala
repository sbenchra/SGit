package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities

object Tag {

  def createTag(tagName:String):Unit={

      val file = new File(Init.RepositoryPath+"/.sgit/refs/tags/"+tagName)
      if(file.exists()) println("This Tag already exists")
      else {
        Branch.commitWrite(file)
      }

    }

  def main(args: Array[String]): Unit = {
    Init.Init()
    Add.add(List(new File("./soufiane")))
    // Status.status()
    //sgit.commands.Commit.commit("d")
    //Log.logP()
   Tag.createTag("Version 2")
    //  Diff.diff()
    //Branch.branchAv()
    //FilesUtilities.deleteContentIndex(Array(" "))

    //println(FilesUtilities.indexContentBis.map(_.head))
    //  print(s)
  }
}
