package sgit.commands

import java.io.File
import sgit.utilities.FilesUtilities
import sgit.{Blob, ObjectBL,Object}

object Add {


  def addFile(file:File): Unit={
    ObjectBL.addObject(Blob(FilesUtilities.readFileContent(file)))
    }
  def addToIndex(file :File) :String={
        if (!file.isFile) ""
        else
      ObjectBL.sha(Blob(FilesUtilities.readFileContent(file)))+file.getAbsolutePath

  }

  def add(lfiles:List[File]):Unit ={
    if (lfiles.isEmpty) print("No files to add")
    else if (lfiles.head.isFile)
    {
      addFile(lfiles.head)
      addToIndex(lfiles.head)
      add(lfiles.tail)
    }



  }

  }

