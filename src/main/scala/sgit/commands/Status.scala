package sgit.commands
import java.io.File
import sgit.{Index, IndexEntry}
import sgit.utilities.FilesUtilities
object Status {


    @scala.annotation.tailrec
    def statusCompare(index:List[IndexEntry], workDirContent:List[IndexEntry]):Unit={
      index match {
        case _ if index.isEmpty =>        print("Your index is empty")
        case _ if workDirContent.isEmpty =>        print("Your directory is empty")
        case _ if !Index.containsBlob(workDirContent.head,index) =>print(workDirContent.head.path+"is untracked")
          statusCompare(index.tail,workDirContent)
        case _ if !Index.fieldInIndex(index.head.path,workDirContent) && !Index.fieldInIndex(index.head.sha,workDirContent) =>
          print(workDirContent.head.path+ "is modified")
          statusCompare(index.tail,workDirContent)
        case _ if Index.containsBlob(index.head,workDirContent) && !Index.containsBlob(workDirContent.head,index) =>
          print(workDirContent.head.path + "is deleted")
          statusCompare(index.tail,workDirContent)
      }


    }





  def status():Unit={

    val indexContent= Index.indexContent(Index.indexContentBis)
    val workingDirFiles= FilesUtilities.filesOfListFiles(List(new File("./soufiane")))




      statusCompare(indexContent,workingDirBlobs(workingDirFiles))

  }

}
