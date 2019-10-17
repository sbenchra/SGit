package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities

object Tag {

  def tagsDirPath(): String =Init.RepositoryPath+"/.sgit/refs/tags/"

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
    Add.add(List(new File("./soufiane")))
    // Status.status()
    sgit.commands.Commit.commit("d")
    //Log.logP()
print(Checkout.checkoutCommit("c35a677958475c8f1dc2891643d4e00db4bade7c"))
    //  Diff.diff()
    //Branch.branchAv()
    //FilesUtilities.deleteContentIndex(Array(" "))

    //println(FilesUtilities.indexContentBis.map(_.head))
    //  print(s)
  }
}
