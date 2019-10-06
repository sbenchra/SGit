package sgit
import java.io.File
import utilities.RepositoryUtilities



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
        //List of contents to fill in files
        val content = List("ref: refs/heads/master","[core]\n\trepositoryformatversion = 0\n\tfilemode = true\n\tbare = false\n\tlogallrefupdates = true","        Unnamed repository; edit this file 'description' to name the repository.","# git ls-files --others --exclude-from=.git/info/exclude\n# Lines that start with '#' are comments.\n# For a project mostly in C, the following would be a good set of\n# exclude patterns (uncomment them if you want to use them):\n# *.[oa]\n# *~")
        RepositoryUtilities.createDirectories(directories)
        RepositoryUtilities.createFiles(files)
        RepositoryUtilities.writeInFiles(files,content)


        println(s"Dépôt SGit vide initialisé dans $dirPath/.sgit")
      }
      else {
        println(s"Dépôt Git existant réinitialisé dans $dirPath/.sgit")
      }
  }

  def main(args: Array[String]): Unit = {
    initializeRepo()
    println( Blob.createBlob(Blob(3,None)))
  }

}
