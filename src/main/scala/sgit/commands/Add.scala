
package sgit.commands
import java.io.File

import sgit.utilities.FilesUtilities
import sgit.{Blob, Index, IndexEntry, ObjectBL, Repository}

object Add {

  //index File
  def IndexFile: File = {
    new File(Repository.getRepository.getAbsolutePath+"./sgit/index")
  }


  def add(lFiles:List[String]):Unit={
    if (lFiles.isEmpty) print("Missing files")
    else {
      addBis(lFiles.map(x=>new File(x)))
    }
  }


  def addFileToDir(file: File): Unit = {

    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
  }

  //Recursive function to add a list of files
  @scala.annotation.tailrec
  def addBis(lFiles: List[File]): Unit = {

    val lFilesBis= FilesUtilities.filesOfListFiles(lFiles)
    lFilesBis match {
      case _ if lFilesBis.isEmpty=>Unit

      case _ if Index.indexContent(FilesUtilities.indexContentBis).map(_.path).diff(Index.workingDirBlobs(Index.workingDirFiles).map(_.path)).nonEmpty =>
        val diffWdirIndex=Index.indexContent(FilesUtilities.indexContentBis).diff(Index.workingDirBlobs(Index.workingDirFiles))
        val toListDiff=listOfIndexListArray(diffWdirIndex)
        val toListDir=listOfIndexListArray(Index.workingDirBlobs(Index.workingDirFiles))
        FilesUtilities.deleteContentIndex(FilesUtilities.deleContentIndexBis(toListDiff.head.mkString(" "),toListDir))
        addBis(lFiles)

        // if the file doesn't exist in the index add it to the index and object Directory
      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index(Index.indexContent(FilesUtilities.indexContentBis))) &&
        !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path,Index(Index.indexContent(FilesUtilities.indexContentBis)) )=>
                  addFileToDir(lFilesBis.head)
                 val indexEntry=Index.fileToIndexEntry(lFilesBis.head)
                  Index.addIndexEntry(indexEntry)
                 addBis(lFilesBis.tail)
        // if
      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index(Index.indexContent(FilesUtilities.indexContentBis))) &&
                 Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path, Index(Index.indexContent(FilesUtilities.indexContentBis)) )=>
                 addFileToDir(lFilesBis.head)
                 Index.modifyIndexContent(Index.shaAndPath(lFilesBis.head),FilesUtilities.indexContentBis)
                 addBis(lFilesBis.tail)

      case _=>  addBis(lFilesBis.tail)
    }



  }

  def listOfIndexListArray(diff:List[IndexEntry]):List[Array[String]]={
    if (diff.isEmpty)List()
    else List(Array(diff.head.path,diff.head.sha))++listOfIndexListArray(diff.tail)
  }





}