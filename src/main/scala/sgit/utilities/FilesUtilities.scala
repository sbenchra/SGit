package sgit.utilities

import java.io.{BufferedWriter, File, FileWriter}
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


  // Returns the file content as a string
  def readFileContent(file: File): String = {
    Source.fromFile(file).mkString
  }

  //Recursive function to list files of a directory and its directories
  def filesOfListFiles(lFile: List[File]): List[File] = {
    lFile match {
      case _ if lFile.isEmpty => List()
      case _ if !lFile.head.exists()=>filesOfListFiles(lFile.tail)
      case _ if lFile.head.isFile => lFile.head :: filesOfListFiles(lFile.tail)
      case _ if lFile.head.isDirectory => filesOfListFiles(lFile.head.listFiles().toList) ++ filesOfListFiles(lFile.tail)
    }

  }

  //Getting the index file
  def IndexFile:File={
    new File(System.getProperty("user.dir")+"/.sgit/index")
  }

  //Function to get an index content
  def indexContentBis:List[Array[String]]={

    Source.fromFile(IndexFile.getAbsolutePath).getLines().toList.map(x=>x.split(" "))

  }

//Function to modify a files content
  def modifyFile(file: File, content: List[Array[String]]): Unit = {
    val tmp=new File("/tmp/temporary.txt")
    if (content.isEmpty) Unit
    else {
      openFileOverWrite(tmp, content.head.mkString(" "))
      modifyFile(file,content.tail)
    }
    val index= new File("./.sgit/index")
    tmp.renameTo(index)


  }
}
