package sgit.utilities

import java.io.{BufferedWriter, File, FileWriter}

object RepositoryUtilities {
  //Recusrive function to create Directories

  def createDirectories(sDirecoties: List[String]): Unit = {
    if (sDirecoties.isEmpty) {
      println("The list of directories is empty")
    }
    else {
      new File(sDirecoties.head).mkdir()

    }

  }

  //Recursive function to create files

  def createFiles(sFiles: List[String]): Unit = {
    if (sFiles.isEmpty) {
      println("The list of files is empty")
    }
    else {
      new File(sFiles.head).createNewFile()
      createFiles(sFiles.tail)

    }
  }

  //Recursive function to write in a file
  //Write an element of the list contents to a file in the list files having the same index
  def writeInFiles(files: List[String], contents: List[String]): Unit = {
    if (files.isEmpty || contents.isEmpty || contents.length != files.length ) {
      println("nothing to write")
    }
    else {
      val file = new File(files.head)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(contents.head)
      bw.close()
      writeInFiles(files.tail, contents.tail)

    }
  }
}
