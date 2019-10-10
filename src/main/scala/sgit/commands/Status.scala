package sgit.commands
import java.io.File
import sgit.{Index, IndexEntry}
import sgit.utilities.FilesUtilities
object Status {

// A recursive function to compare the directory files with index files
    @scala.annotation.tailrec
    def statusCompare(index:List[IndexEntry], workDirContent:List[IndexEntry]):Unit={
      index match {
        case _ if index.isEmpty =>        Unit
        case _ if( workDirContent.isEmpty && index.nonEmpty ) ||( Index.containsIndexEntry(index.head,workDirContent) && !Index.containsIndexEntry(workDirContent.head,index)) =>
          print(index.head.path + " is deleted \n")
          statusCompare(index.tail,workDirContent)
        case _ if !Index.containsIndexEntry(workDirContent.head,index) =>print(workDirContent.head.path+" is untracked")
          statusCompare(index.tail,workDirContent)
        case _ if Index.fieldInIndex(index.head.path,workDirContent) && !Index.fieldInIndex(index.head.sha,workDirContent) =>
          print(workDirContent.head.path+ " is modified")
          statusCompare(index.tail,workDirContent)
        case _ => println(index.head.path+ " is tracked")
      }


    }





  def status():Unit={

    val indexContent= Index.indexContent(FilesUtilities.indexContentBis)
    val workingDirFiles= FilesUtilities.filesOfListFiles(List(new File("./soufiane")))




      statusCompare(indexContent,Index.workingDirBlobs(workingDirFiles))

  }

}
