package sgit.commands
import java.io.File
import java.nio.file.Files

import sgit.Index
import sgit.utilities.FilesUtilities
object Status {

  def workingDirBlobs(lFiles:List[File]) :List[Array[String]]={
    if(lFiles.isEmpty) List()
    else Index.shaAndPath(lFiles.head):: workingDirBlobs(lFiles.tail)
  }
  def containsBlob(workDirField:Array[String],index:List[Array[String]]):Boolean={
    if(workDirField.isEmpty && index.isEmpty) false
    else (workDirField.head.equals(index.head.head) && workDirField(1).equals(index.head(1))) || containsBlob(workDirField,index.tail)
  }

  @scala.annotation.tailrec
  def status(index:List[Array[String]], workDirContent:List[Array[String]]):Unit={
    val soufiane = workDirContent.head
    if (index.isEmpty)
      print("Your index is empty")
    else if (workDirContent.isEmpty)
      print("Your directory is empty")
    else if(!containsBlob(workDirContent.head,index) ){
      print(workDirContent.head(1)+"is untracked")
      status(index.tail,workDirContent)
    }
    else if (!Index.fieldInIndex(index.head.head,workDirContent) && Index.fieldInIndex(index.head(1),workDirContent))
    {
      print(workDirContent.head(1) + "is modified")
      status(index.tail,workDirContent)
    }
    else if (containsBlob(index.head,workDirContent) && !containsBlob(workDirContent.head,index))
    {
      print(workDirContent.head(1) + "is deleted")
      status(index.tail,workDirContent)    }

  }





  def status() :Unit={

    val indexContent= Index.indexContent
    val workingDirFiles= FilesUtilities.filesOfListFiles(List(new File("./soufiane")))




    status(indexContent,workingDirBlobs(workingDirFiles))

  }

}
