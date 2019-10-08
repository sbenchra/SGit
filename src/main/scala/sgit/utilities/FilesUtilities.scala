package sgit.utilities

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.charset.StandardCharsets
import java.util.Base64.getEncoder

import com.google.common.io.BaseEncoding

import scala.io.Source

object
FilesUtilities {
  //Recusrive function to create Directories

  def createDirectories(sDirecoties: List[String]): Unit = {
    if (sDirecoties.isEmpty) {
      println("The list of directories is empty")
    }
    else {
      new File(sDirecoties.head).mkdirs()
      createDirectories(sDirecoties.tail)
    }

  }

  //Recursive function to create files

  def createFiles(sFiles: List[String]): List[File] = {
    if (sFiles.isEmpty)  List()
    else {
      val newFile=new File(sFiles.head)
        newFile.createNewFile()
       newFile::createFiles(sFiles.tail)

    }
  }

  //Recursive function to write in a file
  //Write an element of the list contents to a file in the list files having the same index
  def writeInFiles(files: List[File], contents: List[String]): Unit = {
    if (files.isEmpty || contents.isEmpty || contents.length != files.length) {
        println("nothing to write")
    }
    else {
      fillInFile (files.head,contents.head)
      writeInFiles(files.tail, contents.tail)

    }
  }

  def fillInFile(file:File,content:String) :Unit={
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(content)
    bw.close()
  }


  //Recursive function to write in a file
  //Write an element of the list contents to a file in the list files having the same index
  def writeInFile(file:File, content: List[String]): Unit = {
    if (file.exists()) {
      println("Not found")
    }
    else {
      fillInFile (file,content.head)
      writeInFile(file, content.tail)

    }
  }


  // Returns the file
  def readFileContent(file: File): String = {
    Source.fromFile(file).mkString }

  def getListOfSubDirectories(directoryName: String): List[File] = {
    (new File(directoryName))
      .listFiles
      .filter(_.isFile).toList
  }




}
