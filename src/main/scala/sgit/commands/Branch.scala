package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities

import scala.annotation.tailrec

object Branch {


  def commitWrite(file: File) = {
    val newBranchFile = FilesUtilities.createFiles(List(file.getAbsolutePath))
    val lastCommit = Commit.lastCommit._2
    FilesUtilities.writeInFile(newBranchFile.head, List(lastCommit))
  }

  def branch(branchName:String):Unit={

    val file = new File(Init.RepositoryPath+"/.sgit/refs/heads/"+branchName)
    if(file.exists()) println("This branch already exists")
    else {
      commitWrite(file)
    }


  }


  @tailrec
  def branchAVPrinter(lFiles:List[File]):Unit={
    if (lFiles.isEmpty) Unit
    else
      {
        println(lFiles.head.getName)
        branchAVPrinter(lFiles.tail)
      }
  }

  def branchAv():Unit={
    val branchsFiles=FilesUtilities.filesOfListFiles(List(new File(Init.RepositoryPath+"/.sgit/refs/heads/")))
    branchAVPrinter(branchsFiles)
  }

}
