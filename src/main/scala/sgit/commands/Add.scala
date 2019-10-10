
package sgit.commands
import java.io.File
import sgit.utilities.FilesUtilities
import sgit.{Blob, Index, IndexEntry, ObjectBL}

object Add {
//index File
  def IndexFile: File = {
    new File(System.getProperty("user.dir") + "./sgit/index")
  }
  //Repository path

  def RepositoryPath: String = {
    System.getProperty("user.dir")
  }

// Add a file to objects

  def addFileToDir(file: File): Unit = {

    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
  }

//Recursive function to add a list of files
  @scala.annotation.tailrec
  def add(lFiles: List[File]): Unit = {

    val lFilesBis= FilesUtilities.filesOfListFiles(lFiles)
    lFilesBis match {
      case _ if lFilesBis.isEmpty=>Unit
      case _ if !lFilesBis.head.exists() => add(lFilesBis.tail)

      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index.indexContent(FilesUtilities.indexContentBis)) && !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path, Index.indexContent(FilesUtilities.indexContentBis)) =>
                 addFileToDir(lFilesBis.head)
                 Index.addIndexEntry(lFilesBis.head)
                 add(lFilesBis.tail)

      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index.indexContent(FilesUtilities.indexContentBis)) &&
                 Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path, Index.indexContent(FilesUtilities.indexContentBis)) =>
                 addFileToDir(lFilesBis.head)
                 Index.modifyIndexContent(IndexEntry(Index.shaAndPath(lFilesBis.head).sha,Index.shaAndPath(lFilesBis.head).path),FilesUtilities.indexContentBis)
                 add(lFilesBis.tail)

      case _=>Unit
    }



  }





}