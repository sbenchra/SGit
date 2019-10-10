package sgit

import java.io.File
import sgit.utilities.FilesUtilities
import scala.io.Source

case class IndexEntry(path:String,sha:String)

case class Index(indexEntries:List[IndexEntry])

object Index{



  def indexContent(contentBis:List[Array[String]]): List[IndexEntry]={
    if (contentBis.isEmpty) List()
    else if (contentBis.head.length==2) List(IndexEntry(contentBis.head(1),contentBis.head.head))++indexContent(contentBis.tail)
    else indexContent(contentBis.tail)

  }

  def modifyIndexContent(sha:String, path:String, indexContent:List[Array[String]]):Unit={
    if (indexContent.isEmpty) print("")
    else if (indexContent.head.contains(path)) {
       indexContent.head.update(0,sha)
    }
    else {modifyIndexContent(sha,path,indexContent.tail)}
    FilesUtilities.modifyFile(FilesUtilities.IndexFile,indexContent)


  }

  @scala.annotation.tailrec
  def fieldInIndex(field:String, text:List[IndexEntry]):Boolean={
    if (text.isEmpty) false
    else {
      text.head.sha.equals(field) || text.head.path.equals(field) || fieldInIndex(field,text.tail)}
  }

  def shaAndPath(file:File): IndexEntry= {
    val blob = new Blob(FilesUtilities.readFileContent(file))
    val sha = ObjectBL.sha(blob)
    val path = file.getPath
    IndexEntry(path,sha)
  }

  def addIndexEntry(file: File) : IndexEntry= {
    if (!file.exists()) IndexEntry("", "")
    else {
      val sha = ObjectBL.sha(new Blob(FilesUtilities.readFileContent(file)))
      val path = file.getPath
      FilesUtilities.writeInFile(FilesUtilities.IndexFile,List("\n",s"$sha"," ",s"${path}"))
      IndexEntry(path, sha)
    }
  }

  def workingDirBlobs(lFiles:List[File]) :List[IndexEntry]={
    if(lFiles.isEmpty) List()
    else Index.shaAndPath(lFiles.head):: workingDirBlobs(lFiles.tail)
  }
  @scala.annotation.tailrec
  def containsBlob(workDirField:IndexEntry, index:List[IndexEntry]):Boolean={
    if(index.isEmpty) false
    else
    {
      workDirField.sha.equals(index.head.sha) || index.head.path.equals(workDirField.path) || containsBlob(workDirField,index.tail)
    }
  }


}