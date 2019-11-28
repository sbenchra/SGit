package sgit.commands

import sgit.{Index, IndexEntry}
import sgit.commands.Commit.allFileAreStaged
import sgit.utilities.FilesUtilities

object Status {

  // A recursive function to compare the directory files with index files
  def statusCompare(index: Index, workDirContent: Index): Map[String,String] = {
    index match {
      //if the index is empty
      case _ if index.indexEntries.isEmpty => Map()
      // if the working directory and the index is not empty or the index contains the blob but not the directory
      case _
          if (workDirContent.indexEntries.isEmpty && index.indexEntries.nonEmpty) || !Index
            .containsIndexEntry(
              index.indexEntries.head,
              Index(workDirContent.indexEntries)
            ) =>
        println(index.indexEntries.head.path + " is deleted \n")
        Map(index.indexEntries.head.path ->"deleted")++statusCompare(Index(index.indexEntries.tail), workDirContent)

      // if the directory contains a blob which not exists in index
      case _
          if !Index.containsIndexEntry(
            index.indexEntries.head,
            Index(workDirContent.indexEntries)
          ) =>
        println(workDirContent.indexEntries.head.path + " is untracked")
        Map(index.indexEntries.head.path ->" is untracked")++statusCompare(Index(index.indexEntries.tail), workDirContent)
      // if the index contains the blob's path but not is sha1
      case _
          if Index.fieldInIndex(
            index.indexEntries.head.path,
            Index(workDirContent.indexEntries)
          ) && !Index.fieldInIndex(
            index.indexEntries.head.sha,
            Index(workDirContent.indexEntries)
          ) =>
        println(index.indexEntries.head.path + " is modified")
        Map(index.indexEntries.head.path -> " is modified")++statusCompare(Index(index.indexEntries.tail), workDirContent)
      //if the index entry exists in the working directory index and the working directory index entry exists in the index
      case _
          if Index.containsIndexEntry(index.indexEntries.head, index) && Index
            .containsIndexEntry(index.indexEntries.head, workDirContent) =>
        println(index.indexEntries.head.path + " is tracked")
        statusCompare(
          Index(index.indexEntries.tail),
          Index(workDirContent.indexEntries)
        )
    }

  }

  def status(): Unit = {

val status=statusCompare(Index.indexContent, Index.directoryContent)
    val untracked =
      Index.indexesDiff(Index.indexContent, Index.directoryContent)
    //Unstaged files
    if (Index
          .indexesDiff(Index.indexContent, Index.directoryContent)
          .nonEmpty) {

      Index.entryPrinter(untracked)
    }

    if(status.isEmpty && untracked.isEmpty) println("La copie de travail est propre")



  }

}
