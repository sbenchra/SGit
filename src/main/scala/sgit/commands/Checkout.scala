package sgit.commands

import java.io.File

import sgit.Index
import sgit.utilities.FilesUtilities

object Checkout {

/*
  def checkout(id:String)={

    val logContent=Log.commitAndParent(Log.logContentArray)
    val commits=logContent.keys.toList
    val parents=logContent.values.toList
    val branches=Branch.branchAVPrinter(Branch.branchFiles)
    val tags=Tag.tagFiles()
        if (commits.contains(id) || parents.contains(id)) {    checkoutCommit(id)        }
        else if (branches.contains(id)) {checkoutBranch(id)}
        else if (tags.contains(id)) {checkoutTag(id) }
        else print("Not found tag")
  }

 */

@scala.annotation.tailrec
def addNewIndex(index:Index):Unit={
  if (index.indexEntries.isEmpty)Unit
  else {Index.addIndexEntry(index.indexEntries.head)
        addNewIndex(Index(index.indexEntries.tail))
  }
}
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

      FilesUtilities.deleteRecursively(new File(Init.RepositoryPath+"/soufiane"))
      newDirectory(blobsContent)





  }

}
