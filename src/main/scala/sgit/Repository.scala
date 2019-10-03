package sgit
import java.io.File
import java.io.PrintWriter
import java.nio.file.Paths


object Repository {

  private def isInitialized(path : String) : Boolean ={
    val dir = new File(path)
    dir.listFiles().map(_.getName()).contains(".sgit")
  }


  private def initializeRepo(): Unit = {

      val dirPath = System.getProperty("user.dir")
      if (!isInitialized(dirPath)) {
        //Sequence of directories to create
        val directories = Seq(s"$dirPath/.sgit",s"$dirPath/.sgit/objects/info",s"$dirPath/.sgit/objects/pack",s"$dirPath/.sgit/branches",s"$dirPath/.sgit/refs")
        //List of files to create
        val files = Seq(s"$dirPath/.sgit/HEAD",s"$dirPath/.sgit/config",s"$dirPath/.sgit/description",s"$dirPath/.sgit/index")
        //Create Directories
        directories.foreach((directory) => new File(directory).mkdirs())
        //Create files
        files.foreach((file) => new File(file).createNewFile())
        println("Initialisation done")
      }
      else {
        println("Repository already initialized")
      }
  }

  def main(args: Array[String]): Unit ={
    initializeRepo()
  }
}
