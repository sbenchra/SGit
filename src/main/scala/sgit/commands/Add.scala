
package sgit.commands

import java.io.File
import sgit.utilities.FilesUtilities
import sgit.{Blob, Index, ObjectBL}

object Add {

  def IndexFile: File = {
    new File(System.getProperty("user.dir") + "./sgit/index")
  }

  def RepositoryPath: String = {
    System.getProperty("user.dir")
  }



  def addFileToDir(file: File): Unit = {

    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
  }


  @scala.annotation.tailrec
  def add(lFiles: List[File]): Unit = {

    val lFilesBis= FilesUtilities.filesOfListFiles(lFiles)
    lFilesBis match {
      case _ if lFilesBis.isEmpty=>
                        print("Nothing to add")
      case _ if !lFilesBis.head.exists() => add(lFilesBis.tail)

      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index.indexContent(FilesUtilities.indexContentBis)) && !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path, Index.indexContent(Index.indexContentBis)) =>
                 addFileToDir(lFilesBis.head)
                 Index.addIndexEntry(lFilesBis.head)
                 add(lFilesBis.tail)

      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index.indexContent(FilesUtilities.indexContentBis)) &&
                 Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path, Index.indexContent(FilesUtilities.indexContentBis)) =>
                 addFileToDir(lFilesBis.head)
                 Index.modifyIndexContent(Index.shaAndPath(lFilesBis.head).sha,Index.shaAndPath(lFilesBis.head).path,FilesUtilities.indexContentBis)
                 add(lFilesBis.tail)

      case _=>println("Nothing")
    }



  }





}