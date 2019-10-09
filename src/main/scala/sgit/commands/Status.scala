package sgit.commands
import java.io.File
import java.nio.file.Files

import sgit.Index
import sgit.utilities.FilesUtilities
object Status {

  def workingDirBlobs(lFiles:List[File]) :List[Array[String]]={
    if(lFiles.isEmpty) List()
    else Index.shaAndPath(lFiles.head)::workingDirBlobs(lFiles.tail)
  }
  def fileStatus(indexContent:List[Array[String]],workDirContent:List[Array[String]]) :Unit={
    val index = indexContent
    index match{
      case _ if index.isEmpty && workDirContent.nonEmpty =>{print("Untracked File"+workDirContent.head(1))
                                                            fileStatus(index,workDirContent.tail)}
      case _ if index.nonEmpty && workDirContent.nonEmpty =>{ print("")

      }
    }
  }

  def status :String={

    val indexContent= Index.indexContent
    val workingDirFiles= FilesUtilities.filesOfListFiles(List(Index.IndexFile))
    val workingDirBlobs= workingDirBlobs(workingDirFiles)





  }

}
