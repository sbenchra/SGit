package sgit.commands

import java.io.File

import sgit.Objects.Repository
import sgit.utilities.FilesUtilities

object Branch {
  //headsDir in repository
  def headsDir(): String = Repository.get.getAbsolutePath + "/.sgit/refs/heads/"

  //Function to create a branch
  //@param: branchName:String
  def branch(branchName: String): Unit = {
    val file = new File(headsDir + branchName)
    if (file.exists()) println("This branch already exists")
    else {
      commitWrite(file)
    }
  }
  //Write the last commit id in the branch file
  //@param file:File -> file of the new branch
  def commitWrite(file: File): Unit = {
    val newBranchFile = FilesUtilities.createFiles(List(file.getAbsolutePath))
    val lastCommit = Commit.lastCommitBranch._2
    FilesUtilities.writeInFile(newBranchFile.head, List(lastCommit))
  }

  //Display all branches
  def branchAVPrinter(lFiles: List[File]): List[String] = {
    if (lFiles.isEmpty) List()
    else {
      println(lFiles.head.getName)
      lFiles.head.getName :: branchAVPrinter(lFiles.tail)
    }
  }
//display all branches
  def branchAv(): Unit = {
    val branchs: scala.List[_root_.java.io.File] = branchFiles
    branchAVPrinter(branchs)
  }
  def branchFiles: List[File] = {
    val branchsFiles =
      FilesUtilities.filesOfListFiles(List(new File(headsDir())))
    branchsFiles
  }
}
