package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities

import scala.annotation.tailrec

object Branch {


  def branch(branchName:String):Unit={

    val newBranchFile=FilesUtilities.createFiles(List(Init.RepositoryPath+"/.sgit/refs/heads/"+branchName))
    val lastCommit=Commit.lastCommit._2
    FilesUtilities.writeInFile(newBranchFile.head,List(lastCommit))

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
