package utilities
import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.nio.file.Paths

object RepositoryUtilities {
  //Create Directories

  def createDirectories(sDirecoties: List[String]): Unit = {
    if(sDirecoties.length == 0)
      {
        println("The list of directories is empty")
      }
    else
      {
        new File(sDirecoties.head).mkdir()
        createDirectories(sDirecoties.tail)

      }

  }
  //Create files

  def createFiles(sFiles: List[String]): Unit = {
    if(sFiles.length == 0)
    {
      println("The list of files is empty")
    }
    else
    {
      new File(sFiles.head).createNewFile()
      createFiles(sFiles.tail)

    }  }
  //Write in a file
  def writeInFile(filename:String,text:String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()
  }
}
