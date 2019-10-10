package sgit.commands
import java.io.File
import java.nio.file.Files

import sgit.{Index, IndexEntry}
import sgit.utilities.FilesUtilities
object Status {

    def workingDirBlobs(lFiles:List[File]) :List[IndexEntry]={
      if(lFiles.isEmpty) List()
      else Index.shaAndPath(lFiles.head):: workingDirBlobs(lFiles.tail)
    }
    def containsBlob(workDirField:IndexEntry,index:List[IndexEntry]):Boolean={
      if(index.isEmpty) false
      else
        {
          workDirField.sha.equals(index.head.sha) || index.head.path.equals(workDirField.path) || containsBlob(workDirField,index.tail)
        }
    }

    @scala.annotation.tailrec
    def statusCompare(index:List[IndexEntry], workDirContent:List[IndexEntry]):Unit={
      if (index.isEmpty)
        print("Your index is empty")
      else if (workDirContent.isEmpty)
        print("Your directory is empty")
      else if(!containsBlob(workDirContent.head,index) ){
        print(workDirContent.head.path+"is untracked")
        statusCompare(index.tail,workDirContent)
      }
      else if (!Index.fieldInIndex(index.head.path,workDirContent) && !Index.fieldInIndex(index.head.sha,workDirContent))
      {
        print(workDirContent.head.path+ "is modified")
        statusCompare(index.tail,workDirContent)
      }
      else if (containsBlob(index.head,workDirContent) && !containsBlob(workDirContent.head,index))
      {
        print(workDirContent.head.path + "is deleted")
        statusCompare(index.tail,workDirContent)    }

    }





  def status:Unit={

    val indexContent= Index.indexContent(Index.indexContentBis)
    val workingDirFiles= FilesUtilities.filesOfListFiles(List(new File("./soufiane")))




      statusCompare(indexContent,workingDirBlobs(workingDirFiles))

  }

}
