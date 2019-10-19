package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities
import sgit.{Index, Repository}

object Checkout {

//Main checkout
  def checkout(id: String): Unit = {

    val logContent = Log.commitAndParent(Log.logContentArray)
    val commits = logContent.keys.toList
    val parents = logContent.values.toList
    val branches = Branch.branchAVPrinter(Branch.branchFiles)
    val tags = Tag.tagFiles()
    id match {
      case _ if commits.contains(id) || parents.contains(id) =>
        checkoutCommit(id)
      case _ if branches.contains(id) => checkoutBranch(id)
      case _ if tags.contains(id)     => checkoutTag(id)
      case _                          => print("Id not found")
    }

  }

  //Form a new index File
  @scala.annotation.tailrec
  def addNewIndex(index: Index): Unit = {
    if (index.indexEntries.isEmpty) Unit
    else {
      Index.addIndexEntry(index.indexEntries.head)
      addNewIndex(Index(index.indexEntries.tail))
    }
  }

  //decode blobs in the working directory
  //@param: blobsContent : Map[String,List[String]] -> the blobs and their contents
  def newDirectory(blobsContent: Map[String, List[String]]): Unit = {
    if (blobsContent.isEmpty) Unit
    else {
      val filesPath = blobsContent.keys.toList
      val contents = blobsContent.values.map(_.mkString("\n")).toList
      FilesUtilities.createFiles(filesPath)
      val files = filesPath.map(x => new File(x))
      FilesUtilities.writeInFiles(files, contents)
    }

  }

  //Checkout to commit
  //@param : commitId : String
  def checkoutCommit(commitId: String): Unit = {
    val commitBlobs = Log.constructsCommitMap(Map(commitId -> commitId))
    val indexEntriesMap = Log.constructsIndex(commitBlobs)
    val index = indexEntriesMap(s"$commitId")
    val blobsContent = Diff.blobsAndContent(index)
    val indexFile = FilesUtilities.IndexFile
    if (indexFile.exists()) {
      indexFile.delete()
    }
    //Delete working directory
    FilesUtilities.deleteRecursively(
      new File(Repository.getWorkingDirPath(Init.CureentFile))
    )
    FilesUtilities.createFiles(List(indexFile.getAbsolutePath))
    addNewIndex(index)
    newDirectory(blobsContent)
    println("Basculement sur: " + commitId)
  }
//Checkout Branch
  def checkoutBranch(branchName: String): Unit = {
    val file = new File(Branch.headsDir() + branchName)
    val commitId = FilesUtilities.readFileContent(file).head
    val headFile = new File(Repository.get.getAbsolutePath + "/.sgit/HEAD")
    checkoutCommit(commitId)
    FilesUtilities.modifyFile(headFile, List(Array(branchName)))
  }
  //Check Tag
  def checkoutTag(tagName: String): Unit = {
    val file = new File(Tag.tagsDirPath() + tagName)
    val commitId = FilesUtilities.readFileContent(file).head
    checkoutCommit(commitId)
  }

}
