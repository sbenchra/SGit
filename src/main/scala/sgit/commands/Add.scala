package sgit.commands
import java.io.File

import sgit.utilities.FilesUtilities
import sgit.{Blob, Index, ObjectBL, Repository}

object Add {

  //Index File
  //Return: File-> the index File
  def IndexFile: File = {
    new File(Repository.get.getAbsolutePath + "./sgit/index")
  }

  //Add a list of files
  //@param: lFiles:List[String] -> List of files names
  def add(lFiles: List[String]): Unit = {
    if (lFiles.isEmpty) print("Missing files")
    else {
      addBis(lFiles.map(x => new File(x)))
    }
  }
  //Stage a file
  //@param: file : File -> a file to stage
  def stageFile(file: File): Unit = {
    val blobContent = FilesUtilities.readFileContent(file)
    val blob = Blob(blobContent)
    ObjectBL.addObject(blob)
  }

  //Recursive function to add a list of files
  //@param: lFiles: List[File]-> a list of files
  @scala.annotation.tailrec
  def addBis(lFiles: List[File]): Unit = {

    val lFilesBis = FilesUtilities.filesOfListFiles(lFiles)
    val indexContent = FilesUtilities.indexContentBis
    lFilesBis match {

      case _ if lFilesBis.isEmpty => Unit
      //If a file is a deleted from the working directory
      // Delete from the stage
      case _
          if Index
            .indexContentToIndex(FilesUtilities.indexContentBis)
            .map(_.path)
            .diff(Index.workingDirIndex(Index.workingDirFiles).map(_.path))
            .nonEmpty =>
        val wDirBlobs = Index.workingDirIndex(Index.workingDirFiles)
        val diffWdirIndex =
          Index.indexContentToIndex(indexContent).diff(wDirBlobs)
        val toListDiff = Index.listOfIndexListArray(diffWdirIndex)
        val toListDir = Index.listOfIndexListArray(wDirBlobs)
        FilesUtilities.deleteContentIndex(
          FilesUtilities
            .changeContentIndexBis(toListDiff.head.mkString(" "), toListDir)
        )
        addBis(lFiles)
      // if the file doesn't exist in the index add it to the index and object Directory
      case _
          if !Index.fieldInIndex(
            Index.shaAndPath(lFilesBis.head).sha,
            Index(Index.indexContentToIndex(indexContent))
          ) &&
            !Index.fieldInIndex(
              Index.shaAndPath(lFilesBis.head).path,
              Index(Index.indexContentToIndex(indexContent))
            ) =>
        stageFile(lFilesBis.head)
        val indexEntry = Index.shaAndPath(lFilesBis.head)
        Index.addIndexEntry(indexEntry)
        addBis(lFilesBis.tail)
      // if the path is in the index but not the -> File is modified
      case _
          if !Index.fieldInIndex(
            Index.shaAndPath(lFilesBis.head).sha,
            Index(Index.indexContentToIndex(indexContent))
          ) &&
            Index.fieldInIndex(
              Index.shaAndPath(lFilesBis.head).path,
              Index(Index.indexContentToIndex(indexContent))
            ) =>
        stageFile(lFilesBis.head)
        Index.modifyIndexContent(Index.shaAndPath(lFilesBis.head), indexContent)
        addBis(lFilesBis.tail)

      case _ => addBis(lFilesBis.tail)
    }

  }

}
