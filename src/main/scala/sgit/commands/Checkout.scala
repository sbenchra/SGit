package sgit.commands

import java.io.File

import sgit.{Index, Repository}
import sgit.utilities.FilesUtilities

object Checkout {

//Main checkout
  def checkout(id:String): Unit ={

    val logContent=Log.commitAndParent(Log.logContentArray)
    val commits=logContent.keys.toList
    val parents=logContent.values.toList
    val branches=Branch.branchAVPrinter(Branch.branchFiles)
    val tags=Tag.tagFiles()
        if (commits.contains(id) || parents.contains(id)) {    checkoutCommit(id)        }
        else if (branches.contains(id)) {checkoutBranch(id)}
        else if (tags.contains(id)) {checkoutTag(id) }
        else print("Id not found")
  }


//Form a new index File
@scala.annotation.tailrec
def addNewIndex(index:Index):Unit={
  if (index.indexEntries.isEmpty)Unit
  else {Index.addIndexEntry(index.indexEntries.head)
        addNewIndex(Index(index.indexEntries.tail))
  }
}

  //decode blobs in the working directory
  def newDirectory(blobsContent:Map[String,List[String]]):Unit={
    if (blobsContent.isEmpty) Unit
    else {
      val filesPath=blobsContent.keys.toList
      val contents=blobsContent.values.map(_.mkString("\n")).toList
      FilesUtilities.createFiles(filesPath)
      val files=filesPath.map(x=>new File(x))
      FilesUtilities.writeInFiles(files,contents)
    }

  }

//Checkout commit
  def checkoutCommit(commitId: String): Unit ={

      val commitBlobs= Log.constructsCommitMap(Map(commitId->commitId))
      val indexEntriesMap=Log.constructsIndex(commitBlobs)
      val index=indexEntriesMap(s"$commitId")
      val blobsContent=Diff.blobsAndContent(index)
      val indexFile=FilesUtilities.IndexFile
      if(indexFile.exists()){
        indexFile.delete()
      }
      FilesUtilities.createFiles(List(indexFile.getAbsolutePath))
      addNewIndex(index)
      FilesUtilities.deleteRecursively(new File(Repository.getWorkingDirPath(Init.CureentFile)))
      newDirectory(blobsContent)
    println("Basculement sur le commit : "+commitId)


  }
//Checkout Branch
  def checkoutBranch(branchName:String):Unit={
    val file= new File(Branch.headsDir()+branchName)
   val commitId=FilesUtilities.readFileContent(file).head
      val headFile= new File(Repository.get.getAbsolutePath+"/.sgit/HEAD")
      checkoutCommit(commitId)
      FilesUtilities.modifyFile(headFile,List(Array(branchName)))
    println("Basculement sur la branche : "+branchName)
  }
  //Check Tag
  def checkoutTag(tagName:String):Unit={
    val file= new File(Tag.tagsDirPath()+tagName)
     val commitId=FilesUtilities.readFileContent(file).head
      checkoutCommit(commitId)

  }


}
