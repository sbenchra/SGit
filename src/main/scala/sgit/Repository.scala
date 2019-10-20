package sgit

import java.io.File

import sgit.commands.Init
import sgit.utilities.FilesUtilities

object Repository {

  //Get the working directory
  def get: File = {
    new File(getWorkingDirPath(Init.CureentFile))
  }

  //Check the existence of an array of files
  //@parame: aFile -> Array of files
  //Return : if files contains .sgit or not
  @scala.annotation.tailrec
  def containsRepo(aFile: Array[File]): Boolean = {
    if (aFile.isEmpty) false
    else {
      aFile.head.getAbsolutePath.contains(".sgit") || containsRepo(aFile.tail)
    }

  }

  //Get the working directory path
  //@param: file:File -> a file
  //Return : String: The working directory path
  @scala.annotation.tailrec
  def getWorkingDirPath(file: File): String = {
    if (!file.exists()) ""
    else if (containsRepo(file.listFiles())) file.getAbsolutePath
    else {
      val parent = new File(file.getParent)
      getWorkingDirPath(parent)
    }
  }

  //Initialize the repository
  //@param: path :String-> the working directory path
  def initializeRepo(dirPath: String): Unit = {

    if (!isInitialized(dirPath)) {

      //List of directories to create
      val directories = List(
        s"$dirPath/.sgit",
        s"$dirPath/.sgit/info",
        s"$dirPath/.sgit/objects/info",
        s"$dirPath/.sgit/objects/pack",
        s"$dirPath/.sgit/branches",
        s"$dirPath/.sgit/refs/tags",
        s"$dirPath/.sgit/refs/heads"
      )
      //List of files to create
      val files = List(
        s"$dirPath/.sgit/HEAD",
        s"$dirPath/.sgit/config",
        s"$dirPath/.sgit/description",
        s"$dirPath/.sgit/info/exclude",
        s"$dirPath/.sgit/index",
        s"$dirPath/.sgit/logs"
      )
      //List of contents to fill in files
      val content = List(
        "ref: refs/heads/master",
        "[core]\n\trepositoryformatversion = 0\n\tfilemode = true\n\tbare = false\n\tlogallrefupdates = true",
        "        Unnamed repository; edit this file 'description' to name the repository.",
        "# git ls-files --others --exclude-from=.git/info/exclude\n# Lines that start with '#' are comments.\n# For a project mostly in C, the following would be a good set of\n# exclude patterns (uncomment them if you want to use them):\n# *.[oa]\n# *~",
        "",
        ""
      )
      FilesUtilities.createDirectories(directories)
      val createdFiles = FilesUtilities.createFiles(files)
      FilesUtilities.writeInFiles(createdFiles, content)

      println(s"Dépôt SGit vide initialisé dans $dirPath/.sgit")
    } else {
      println(s"Dépôt Git existant réinitialisé dans $dirPath/.sgit")
    }
  }

  //Check the existence of the repository
  //@param: path :String-> the working directory path
  //Return : Boolean
  def isInitialized(path: String): Boolean = {
    val dir = new File(path)
    dir.listFiles().map(_.getName()).contains(".sgit")
  }

}
