package sgit.commands
import java.io.File
import sgit.{Index, IndexEntry}
import sgit.utilities.FilesUtilities
object Status {

// A recursive function to compare the directory files with index files
    @scala.annotation.tailrec
    def statusCompare(index:Index, workDirContent:Index):Unit={
      index match {
          //if the index is empty
        case _ if index.indexEntries.isEmpty =>        Unit
          // if the working directory and the index is not empty or the index contains the blob but not the directory
        case _ if ( workDirContent.indexEntries.isEmpty && index.indexEntries.nonEmpty ) ||( Index.containsIndexEntry(index.indexEntries.head,Index(workDirContent.indexEntries)) && !Index.containsIndexEntry(workDirContent.indexEntries.head,Index(index.indexEntries))) =>
          println(index.indexEntries.head.path + " is deleted \n")
          statusCompare(Index(index.indexEntries.tail),workDirContent)
          // if the directory contains a blob which not exists in index
        case _ if !Index.containsIndexEntry(workDirContent.indexEntries.head,Index(index.indexEntries) )=>
          println(workDirContent.indexEntries.head.path+" is untracked")
          statusCompare(Index(index.indexEntries.tail),workDirContent)
          // if the index contains the blob's path but not is sha1
        case _ if Index.fieldInIndex(index.indexEntries.head.path,Index(workDirContent.indexEntries)) && !Index.fieldInIndex(index.indexEntries.head.sha,Index(workDirContent.indexEntries)) =>
          println(workDirContent.indexEntries.head.path+ " is modified")
          statusCompare(Index(index.indexEntries.tail),workDirContent)
          //if the index entry exists in the working directory index and the working directory index entry exists in the index
        case _ if Index.containsIndexEntry(workDirContent.indexEntries.head,index) && Index.containsIndexEntry(index.indexEntries.head,workDirContent) =>
          println(workDirContent.indexEntries.head.path +" is tracked")
        case _ => Unit
      }


    }





  def status():Unit={

    val indexContent= Index(Index.indexContent(FilesUtilities.indexContentBis))
    val workingDirFiles= FilesUtilities.filesOfListFiles(List(new File("./soufiane")))
    val directoryContent=Index(Index.workingDirBlobs(workingDirFiles))


      statusCompare(indexContent,directoryContent)

  }

}
