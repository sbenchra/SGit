
package sgit.commands

import java.io.File

import sgit.commands.Add.add

import scala.io.Source
import sgit.utilities.FilesUtilities
import sgit.{Blob, Index, IndexEntry, Object, ObjectBL}

object Add {

  def IndexFile: File = {
    new File(System.getProperty("user.dir") + "./sgit/index")
  }

  def RepositoryPath: String = {
    System.getProperty("user.dir")
  }


  //Add File to ./sgit/pbjects

  def addFileToDir(file: File): Unit = {

    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
  }


  def add(lFiles: List[File]): Unit = {

    val lFilesBis= FilesUtilities.filesOfListFiles(lFiles)
    lFilesBis match {
      case _ if lFilesBis.isEmpty => print("Nothing to add")
      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head)(0), Index.indexContent) && !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head)(1), Index.indexContent) =>
        addFileToDir(lFilesBis.head)
        Index.addIndexEntry(lFilesBis.head)
        add(lFilesBis.tail)

      case _ if !Index.fieldInIndex(Index.shaAndPath(lFilesBis.head)(0), Index.indexContent) && Index.fieldInIndex(Index.shaAndPath(lFilesBis.head)(1), Index.indexContent) =>
        addFileToDir(lFilesBis.head)
        Index.modifyIndexContent(Index.shaAndPath(lFilesBis.head)(0),Index.shaAndPath(lFilesBis.head)(1),Index.indexContent)
        add(lFilesBis.tail)
      case _=>println("Nothing")
    }



  }





}