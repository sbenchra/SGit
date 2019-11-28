package sgit.utilities

import java.io.{BufferedWriter, File, FileWriter}

import sgit.Repository
import sgit.commands.Init

import scala.io.Source

//Layer interacting with the files system
object FilesUtilities {

  //Recusrive function to create Directories
  //@param:sDirectories : List[String] -> list of directories
  @scala.annotation.tailrec
  def createDirectories(sDirecoties: List[String]): Unit = {
    if (sDirecoties.isEmpty) Unit
    else {
      new File(sDirecoties.head).mkdirs()
      createDirectories(sDirecoties.tail)
    }

  }

  //Recursive function to create files
  //@param: sFiles: List[String] -> List of files
  //Return : list of files created
  def createFiles(sFiles: List[String]): List[File] = {
    if (sFiles.isEmpty) List()
    else {
      val newFile = new File(sFiles.head)
      FilesUtilities.createDirectories(List(newFile.getParent))
      newFile.createNewFile()
      newFile :: createFiles(sFiles.tail)

    }
  }

  //Recursive function to write in a file
  //Write an element of the list contents to a file in the list files having the same index
  //@param:files :List[File] -> list of files
  //@param:contents: List[String] -> contents of files
  @scala.annotation.tailrec
  def writeInFiles(files: List[File], contents: List[String]): Unit = {
    if (files.isEmpty || contents.isEmpty || contents.length != files.length)
      Unit
    else {
      openFileOverWrite(files.head, contents.head)
      writeInFiles(files.tail, contents.tail)

    }
  }

  //Function to open a file and overwrite a content
  //@param: file:File -> file to overwrite
  //@param: content:String -> content to overwrite
  def openFileOverWrite(file: File, content: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(file, true))
    bw.write(content)
    bw.close()
  }

  //Recursive function to write in a file
  //Write an element of the list contents to a file in the list files having the same index
  //@param: file:File -> file to overwrite
  //@param: content:List[String] -> content to overwrite
  @scala.annotation.tailrec
  def writeInFile(file: File, content: List[String]): Unit = {
    if (file.exists() && content.nonEmpty) {
      openFileOverWrite(file, content.head)
      writeInFile(file, content.tail)
    } else Unit
  }

  //Recursive function to delete files
  //file to delete
  //@param: file:File -> file to delete
  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory) {
      val files = file.listFiles.filter(x => x.getName != ".sgit")
      files.foreach(deleteRecursively)
    } else if (file.exists && !file.delete) {
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
  }

  //Recursive function to list files of a directory and its directories
  //@param: lFile:List[File] -> list of files
  //@return : List[File] -> list of files and directories
  def filesOfListFiles(lFile: List[File]): List[File] = {
    lFile match {
      case _ if lFile.isEmpty => List()
      case _ if !lFile.head.exists() =>
        print(lFile.head.getName + " is not found ")
        filesOfListFiles(lFile.tail)
      case _ if lFile.head.isFile => lFile.head :: filesOfListFiles(lFile.tail)
      case _ if lFile.head.isDirectory =>
        filesOfListFiles(lFile.head.listFiles().toList) ++ filesOfListFiles(
          lFile.tail
        )
      case _ => List()
    }

  }

  //Index content
  def indexContentBis: List[Array[String]] = {

    fileContentList(IndexFile.getAbsolutePath)

  }

  //Getting the index file
  def IndexFile: File = {
    val file = new File(Repository.get.getAbsolutePath + "/.sgit/index")
    if (file.exists()) file
    else {
      new File("")
      println("Index not found make sure you initialize")
      new File("")
    }

  }

  //Function to get an index content
  //Split lines of contents of words
  //@param:filePath:String-> file path
  def fileContentList(filePath: String): List[Array[String]] = {
    Source.fromFile(filePath).getLines().toList.map(x => x.split(" "))
  }

  //Function to modify a files content
  def modifyFile(file: File, content: List[Array[String]]): Unit = {
    val tmp = new File("/tmp/temporary.txt")
    if (content.isEmpty) Unit
    else {
      openFileOverWrite(tmp, content.head.mkString(" ") + "\n")
      modifyFile(file, content.tail)
    }
    tmp.renameTo(file)

  }

  //Write a commit message in the dfile MSG_COMMIt
  def writeCommitMessage(msg: String): Unit = {
    val fileMsg = new File(
      Repository.getWorkingDirPath(Init.CureentFile) + "/.sgit/MSG_COMMIT"
    )
    if (fileMsg.exists()) modifyFile(fileMsg, List(Array(msg)))
    else {
      openFileOverWrite(fileMsg, msg)
    }
  }

  //Change the commit id of the branch
  def changeBranchSha(newSha: String, branch: File): Unit = {
    if (branch.exists()) modifyFile(branch, List(Array(newSha)))
    else {
      openFileOverWrite(branch, newSha)
    }
  }
  //Get the content of the object
  //@param : sha:String -> sha code of the objects
  //@return : List[String] -> lines of the content
  def contentObject(sha: String): List[String] = {
    if(sha=="19011995") List()
    else {
      val filePath = Repository.getWorkingDirPath(Init.CureentFile) + "/.sgit/objects/" + sha
        .take(2) + "/" + sha
        .takeRight(38)
      readFileContent(new File(filePath))

    }

  }

  // Returns the file content as a string
  def readFileContent(file: File): List[String] = {
    Source.fromFile(file).getLines().toList
  }
  //Delete a field
  //@param: dif:String-> the change in the content
  //@param: content : List[Array[String]] -> the content of working directory content
  def changeContentIndexBis(
    dif: String,
    content: List[Array[String]]
  ): List[Array[String]] = {
    if (content.isEmpty) List(Array())
    else if (!content.head.contains(dif))
      content.head +: changeContentIndexBis(dif, content.tail)
    else changeContentIndexBis(dif, content.tail)
  }

  def deleteContentIndex(content: List[Array[String]]): Unit = {
    modifyFile(IndexFile, content)
  }

}
