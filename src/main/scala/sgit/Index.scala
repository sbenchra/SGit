package sgit

import java.io.File
import sgit.utilities.FilesUtilities

//An index line is index entry composed of the file path and its sha
case class IndexEntry(path: String, sha: String)
// An index is list of index entries
case class Index(indexEntries: List[IndexEntry])

object Index {
  //Get the index content
  def indexContent: Index =
    Index(stageContentToIndexEntries(FilesUtilities.indexContentBis))
  //To change
  def workingDirFiles: List[File] =
    FilesUtilities.filesOfListFiles(List(new File("TestDir")))
  //Working directory content as index
  def directoryContent = Index(workingDirIndex(workingDirFiles))

  //Function to tranform an index content to list of index entries
  // @param: contentBis->List of index lines as arrays
  //return list of index entries
  def stageContentToIndexEntries(
    contentBis: List[Array[String]]
  ): List[IndexEntry] = {
    contentBis match {
      case _ if contentBis.isEmpty => List()
      case _ if contentBis.head.length == 2 =>
        val indexLine = contentBis.head
        val path = indexLine.head
        val sha = contentBis.head(1)
        List(IndexEntry(path, sha)) ++ stageContentToIndexEntries(
          contentBis.tail
        )
      case _ => stageContentToIndexEntries(contentBis.tail)
    }
  }
  //Function to modify an index
  //@param: newIndexEntry-> Index Entry to update
  def modifyIndexContent(newIndexEntry: IndexEntry,
                         indexContent: List[Array[String]]): Unit = {
    if (indexContent.isEmpty) Unit
    else if (indexContent.head.contains(newIndexEntry.path)) {
      indexContent.head.update(1, newIndexEntry.sha)
    } else { modifyIndexContent(newIndexEntry, indexContent.tail) }
    val indexFile = FilesUtilities.IndexFile
    FilesUtilities.modifyFile(indexFile, indexContent)

  }
  //Recursive function to check the field in an index
  //@param : field -> sha or path
  //@param : index -> Index of the repository
  //Return a boolean
  @scala.annotation.tailrec
  def fieldInIndex(field: String, index: Index): Boolean = {
    if (index.indexEntries.isEmpty) false
    else {
      index.indexEntries.head.sha.equals(field) || index.indexEntries.head.path
        .equals(field) || fieldInIndex(field, Index(index.indexEntries.tail))
    }
  }
  //Function to get a file as an Index Entry
  //@param : file -> a file to transform
  //Return : IndexEntry
  def shaAndPath(file: File): IndexEntry = {
    if (!file.exists()) IndexEntry("", "")
    else {
      val fileContent = FilesUtilities.readFileContent(file)
      val blob = new Blob(fileContent)
      val sha = ObjectBL.sha(blob)
      val path = file.getAbsolutePath
      IndexEntry(path, sha)
    }
  }

  //Fill an Index Entry to the index File
  //@param: indexEntry -> Index entry
  def addIndexEntry(indexEntry: IndexEntry): Unit = {
    val indexFile = FilesUtilities.IndexFile
    FilesUtilities.writeInFile(
      indexFile,
      List("\n", s"${indexEntry.path}", " ", s"${indexEntry.sha}")
    )
  }

  //Preparing the index of the working direcotry
  //@param : lFiles-> list of files
  //Return : List of index entries
  def workingDirIndex(lFiles: List[File]): List[IndexEntry] = {
    if (lFiles.isEmpty) List()
    else {
      shaAndPath(lFiles.head) :: workingDirIndex(lFiles.tail)
    }
  }

  //Check if the workDirField is contained in the index
  @scala.annotation.tailrec
  def containsIndexEntry(workDirField: IndexEntry, index: Index): Boolean = {
    if (index.indexEntries.isEmpty) false
    else {
      workDirField.sha.equals(index.indexEntries.head.sha) || index.indexEntries.head.path
        .equals(workDirField.path) || containsIndexEntry(
        workDirField,
        Index(index.indexEntries.tail)
      )
    }
  }

  def listOfIndexListArray(diff: List[IndexEntry]): List[Array[String]] = {
    if (diff.isEmpty) List()
    else
      List(Array(diff.head.path, diff.head.sha)) ++ Index.listOfIndexListArray(
        diff.tail
      )
  }
}
