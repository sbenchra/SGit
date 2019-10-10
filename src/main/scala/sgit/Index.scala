package sgit

import java.io.File
import sgit.utilities.FilesUtilities
import scala.io.Source

case class IndexEntry(path:String,sha:String)

case class Index(indexEntries:List[IndexEntry])

object Index{


  def IndexFile:File={
    new File(System.getProperty("user.dir")+"/.sgit/index")
  }

  def indexContent:List[Array[String]]={

    Source.fromFile(IndexFile.getAbsolutePath).getLines().toList.map(x=>x.split(" "))

  }

  def modifyIndexContent(sha:String, path:String, indexContent:List[Array[String]]):Unit={
    if (indexContent.isEmpty) print("")
    else if (indexContent.head.contains(path)) {
      indexContent.head.update(0,sha)
    }
    else {modifyIndexContent(sha,path,indexContent.tail)}
    FilesUtilities.modifyFile(IndexFile,indexContent)


  }

  @scala.annotation.tailrec
  def fieldInIndex(field:String, text:List[Array[String]]):Boolean={
    if (text.isEmpty) false

    else {

      text.head.contains(field) || fieldInIndex(field,text.tail)}

  }

  def shaAndPath(file:File): Array[String]= {
    val blob = new Blob(FilesUtilities.readFileContent(file))
    val sha = ObjectBL.sha(blob)
    val path = file.getPath
    Array(sha,path)
  }

  def addIndexEntry(file: File) : IndexEntry= {
    if (!file.exists()) IndexEntry("", "")
    else {
      val sha = ObjectBL.sha(new Blob(FilesUtilities.readFileContent(file)))
      val path = file.getPath
      FilesUtilities.writeInFile(IndexFile,List("\n",s"$sha"," ",s"${path}"))
      IndexEntry(path, sha)
    }
  }



}