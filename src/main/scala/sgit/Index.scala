package sgit

import java.io.File

import sgit.commands.Init
import sgit.utilities.FilesUtilities

import scala.io.Source

case class IndexEntry(path:String,sha:String)

case class Index(indexEntries:List[IndexEntry])

object Index{

  def indexContent:Index= Index(Index.indexContent(FilesUtilities.indexContentBis))
//To change
  def workingDirFiles: List[File] = FilesUtilities.filesOfListFiles(List(new File("soufiane")))

  def directoryContent=Index(Index.workingDirBlobs(workingDirFiles))

//Function to tranform an index content to list of index entries
  def indexContent(contentBis:List[Array[String]]): List[IndexEntry]={
    if (contentBis.isEmpty) List()
    else if (contentBis.head.length==2) List(IndexEntry(contentBis.head.head,contentBis.head(1)))++indexContent(contentBis.tail)
    else indexContent(contentBis.tail)

  }
//Function to modify an index
  def modifyIndexContent(newIndexEntry: IndexEntry, indexContent:List[Array[String]]):Unit={
    if (indexContent.isEmpty) Unit
    else if (indexContent.head.contains(newIndexEntry.path)) {
       indexContent.head.update(1,newIndexEntry.sha)
    }
    else {modifyIndexContent(newIndexEntry,indexContent.tail)}
    FilesUtilities.modifyFile(FilesUtilities.IndexFile,indexContent)


  }
//Recursive function to check the field in an index

  @scala.annotation.tailrec
  def fieldInIndex(field:String, index:Index):Boolean={
    if (index.indexEntries.isEmpty) false
    else {
      index.indexEntries.head.sha.equals(field) || index.indexEntries.head.path.equals(field) || fieldInIndex(field,Index(index.indexEntries.tail))}
  }
//Function to tranform a file to an index Entry
  def shaAndPath(file:File): IndexEntry= {
    val blob = new Blob(FilesUtilities.readFileContent(file))
    val sha = ObjectBL.sha(blob)
    val path = file.getAbsolutePath
    IndexEntry(path,sha)
  }

  def fileToIndexEntry(file:File):IndexEntry= {
    if (!file.exists()) IndexEntry("", "")
    else {
      shaAndPath(file)

    }
  }



//Tranform a file to and index entry and fill it in Index file
  def addIndexEntry(indexEntry: IndexEntry) : Unit= {

      FilesUtilities.writeInFile(FilesUtilities.IndexFile,List("\n",s"${indexEntry.path}"," ",s"${indexEntry.sha}"))
    }

  //Function to transform the list of working directory files to list of entries
  def workingDirBlobs(lFiles:List[File]) :List[IndexEntry]={
    if(lFiles.isEmpty) List()
    else Index.shaAndPath(lFiles.head):: workingDirBlobs(lFiles.tail)
  }
  //Check if the workDirField is contained in the index
  @scala.annotation.tailrec
  def containsIndexEntry(workDirField:IndexEntry, index:Index):Boolean={
    if(index.indexEntries.isEmpty) false
    else
    {
      workDirField.sha.equals(index.indexEntries.head.sha) || index.indexEntries.head.path.equals(workDirField.path) || containsIndexEntry(workDirField,Index(index.indexEntries.tail))
    }
  }




}