package utilities
import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.nio.file.Paths

object RepositoryUtilities {
  //Create Directories

  def createDirectories(sDirecoties: Seq[String]): Unit = {
    sDirecoties.foreach((directory) => new File(directory).mkdirs())
  }
  //Create files

  def createFiles(sFiles: Seq[String]): Unit = {
    sFiles.foreach((file) => new File(file).createNewFile())
  }
  //Write in a file

  def writeInFile(filename:String,text:String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()
  }
}
