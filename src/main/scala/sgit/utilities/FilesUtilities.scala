package sgit.utilities

import java.io.{BufferedWriter, File, FileWriter}

import javax.swing.text.AbstractDocument.Content
import sgit.commands.{Init, Status}

import scala.io.Source



//Layer interacting with the files system
object
FilesUtilities {
  //Recusrive function to create Directories

  @scala.annotation.tailrec
  def createDirectories(sDirecoties: List[String]): Unit = {
    if (sDirecoties.isEmpty) Unit
    else {
      new File(sDirecoties.head).mkdirs()
      createDirectories(sDirecoties.tail)
    }

  }

  //Recursive function to create files

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
  @scala.annotation.tailrec
  def writeInFiles(files: List[File], contents: List[String]): Unit = {
    if (files.isEmpty || contents.isEmpty || contents.length != files.length) Unit
    else {
      openFileOverWrite(files.head, contents.head)
      writeInFiles(files.tail, contents.tail)

    }
  }

  //Function to open a file and overwrite a content
  def openFileOverWrite(file: File, content: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(file, true))
    bw.write(content)
    bw.close()
  }


  //Recursive function to write in a file
  //Write an element of the list contents to a file in the list files having the same index
  @scala.annotation.tailrec
  def writeInFile(file: File, content: List[String]): Unit = {
    if (file.exists() && content.nonEmpty) {
      openFileOverWrite(file, content.head)
      writeInFile(file, content.tail)
    }
    else Unit
  }

  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory) {
      val files=file.listFiles.filter(x=>x.getName!=".sgit")
        files.foreach(deleteRecursively)
    }
    else if (file.exists && !file.delete) {
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
  }
  // Returns the file content as a string
  def readFileContent(file: File): List[String] = {
    Source.fromFile(file).getLines().toList
  }

  //Recursive function to list files of a directory and its directories
  def filesOfListFiles(lFile: List[File]): List[File] = {
    lFile match {
      case _ if lFile.isEmpty => List()
      case _ if !lFile.head.exists()=>
        print(lFile.head.getName+" is not found ")
        filesOfListFiles(lFile.tail)

      case _ if lFile.head.isFile => lFile.head :: filesOfListFiles(lFile.tail)
      case _ if lFile.head.isDirectory => filesOfListFiles(lFile.head.listFiles().toList) ++ filesOfListFiles(lFile.tail)
    }

  }

  //Getting the index file
  def IndexFile:File={
    new File(System.getProperty("user.dir")+"/.sgit/index")
  }

  //Function to get an index content

  def fileContentList(filePath:String): List[Array[String]]={
    Source.fromFile(filePath).getLines().toList.map(x=>x.split(" "))
  }

  def indexContentBis:List[Array[String]]={

    fileContentList(IndexFile.getAbsolutePath)

  }

//Function to modify a files content
  def modifyFile(file: File, content: List[Array[String]]): Unit = {
    val tmp=new File("/tmp/temporary.txt")
    if (content.isEmpty) Unit
    else {
      openFileOverWrite(tmp, content.head.mkString(" ")+"\n")
      modifyFile(file,content.tail)
    }
    tmp.renameTo(file)

  }
//Write a commit message in the dfile MSG_COMMIt
  def writeCommitMessage(msg:String):Unit={
    val fileMsg=new File(Init.CurrentDirPath+"/.sgit/MSG_COMMIT")
    if(fileMsg.exists()) modifyFile(fileMsg,List(Array(msg)))
    else {
      openFileOverWrite(fileMsg,msg)
    }
  }
//Change the commit id of the branch
  def changeBranchSha(newSha:String,branch:File):Unit={

    if(branch.exists()) modifyFile(branch,List(Array(newSha)))
    else {
      openFileOverWrite(branch,newSha)
    }
  }

  def contentObject(sha:String):List[String]={
    val filePath=Init.CurrentDirPath+"/.sgit/objects/"+sha.take(2)+"/"+sha.takeRight(38)
    readFileContent(new File(filePath))

  }

  def deleContentIndexBis(dif : String,content:List[Array[String]]):List[Array[String]]={
    if (content.isEmpty) List(Array())
    else if (!content.head.contains(dif)) content.head+:deleContentIndexBis(dif,content.tail)
    else deleContentIndexBis(dif,content.tail)
  }

  def deleteContentIndex(content:List[Array[String]]):Unit={
    modifyFile(IndexFile,content)
  }


}
