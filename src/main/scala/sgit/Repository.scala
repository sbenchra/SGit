package sgit
import java.io.File
import utilities.RepositoryUtilities
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
        val directories = List(s"$dirPath/.sgit",s"$dirPath/.sgit/info",s"$dirPath/.sgit/objects/info",s"$dirPath/.sgit/objects/pack",s"$dirPath/.sgit/branches",s"$dirPath/.sgit/refs")
        //List of files to create
        val files = List(s"$dirPath/.sgit/HEAD",s"$dirPath/.sgit/config",s"$dirPath/.sgit/description",s"$dirPath/.sgit/info/exclude")
        RepositoryUtilities.createDirectories(directories)
        RepositoryUtilities.createFiles(files)
        RepositoryUtilities.writeInFile(files(0),"ref: refs/heads/master")
        RepositoryUtilities.writeInFile(files(1),"[core]\n\trepositoryformatversion = 0\n\tfilemode = true\n\tbare = false\n\tlogallrefupdates = true")
        RepositoryUtilities.writeInFile(files(2),"        Unnamed repository; edit this file 'description' to name the repository.")
        RepositoryUtilities.writeInFile(files(3),"# git ls-files --others --exclude-from=.git/info/exclude\n# Lines that start with '#' are comments.\n# For a project mostly in C, the following would be a good set of\n# exclude patterns (uncomment them if you want to use them):\n# *.[oa]\n# *~")
        println(s"Dépôt SGit vide initialisé dans $dirPath/.sgit")
      }
      else {
        println(s"Dépôt Git existant réinitialisé dans $dirPath/.sgit")
      }
  }

  def main(args: Array[String]): Unit ={
    initializeRepo()
  }
}
