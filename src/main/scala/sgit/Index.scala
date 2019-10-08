package sgit

import java.io.File

import sgit.utilities.FilesUtilities

case class IndexEntry(path:String,sha:String)

case class Index(indexEntries:List[IndexEntry])

object Index{


  def IndexFile:File={
    new File(System.getProperty("user.dir")+"./sgit/index")
  }


  def encodeIndexEntries(files: List[File]) : List[IndexEntry]= {

    if (files.isEmpty) {
      List(IndexEntry("", ""))
    }
    else {
      val sha = ObjectBL.sha(new Blob(FilesUtilities.readFileContent(files.head)))
      val path = files.head.getAbsolutePath
      IndexEntry(path, sha) :: encodeIndexEntries(files.tail)
    }
  }
/
  def addIndexToFile(index:Index):Unit={
      if (index.indexEntries.isEmpty) print("")
      else {
        FilesUtilities.writeInFile(IndexFile,List("\n",index.indexEntries.mkString("\t")))
        addIndexToFile(Index(index.indexEntries.tail))
      }}
*





}