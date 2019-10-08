
package sgit.commands

import java.io.File
import scala.io.Source
import sgit.utilities.FilesUtilities
import sgit.{Blob, Index, IndexEntry, Object, ObjectBL}

object Add {
  def IndexFile:File={
    new File(System.getProperty("user.dir")+"./sgit/index")
  }

  def RepositoryPath:String={
    System.getProperty("user.dir")
  }


//Add File to ./sgit/pbjects

  def addFileToDir(file:File): Unit={
    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
    }

  def fieldsInIndex(sha:String,path:String,text:List[Array[String]]):Boolean={
    if (text.isEmpty) false
    else text.head.contains(sha) || text.head.contains(path) || fieldsInIndex(sha,path,text.tail)

  }

  def blobInIndex(file:File,index:File):Boolean={
    val blob=new Blob(FilesUtilities.readFileContent(file))
    val sha=ObjectBL.sha(blob)
    val path=file.getAbsolutePath
    val text=Source.fromFile(path).getLines().toList.map(x=>x.split(" "))
    fieldsInIndex(sha,path,text)
  }


  def add(lFiles:List[File]):Unit ={
    Index.encodeIndexEntries(lFiles)
    if (lFiles.isEmpty) print("")
    else if (lFiles.head.isFile && !blobInIndex(lFiles.head,IndexFile))
    {
      addFileToDir(lFiles.head)
      add(lFiles.tail)
    }



  }

  }

