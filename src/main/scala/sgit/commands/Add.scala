
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



  def addFileToDir(file: File): Unit = {

    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
  }

//Recursive function to add a list of files
  @scala.annotation.tailrec
  def add(lFiles: List[File]): Unit = {

    val lFilesBis= FilesUtilities.filesOfListFiles(lFiles)
    lFilesBis match {
      case _ if lFilesBis.isEmpty=>Unit
        // if the file doesn't exist in the index add it to the index and object Directory
      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index(Index.indexContent(FilesUtilities.indexContentBis))) && !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path,Index(Index.indexContent(FilesUtilities.indexContentBis)) )=>
                  addFileToDir(lFilesBis.head)
                 Index.addIndexEntry(lFilesBis.head)
                 add(lFilesBis.tail)
        // if
      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).sha, Index(Index.indexContent(FilesUtilities.indexContentBis))) &&
                 Index.fieldInIndex(Index.shaAndPath(lFilesBis.head).path, Index(Index.indexContent(FilesUtilities.indexContentBis)) )=>
                 addFileToDir(lFilesBis.head)
                 Index.modifyIndexContent(IndexEntry(Index.shaAndPath(lFilesBis.head).path,Index.shaAndPath(lFilesBis.head).sha),FilesUtilities.indexContentBis)
                 add(lFilesBis.tail)

      case _=>  add(lFilesBis.tail)
    }



  }





}