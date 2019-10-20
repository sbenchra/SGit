package sgit.commands

import java.io.File

import sgit.Repository

object Init {

  def CureentFile: File = {
    new File(CurrentDirPath)
  }

  def CurrentDirPath: String = {
    System.getProperty("user.dir")
  }
//Initialize the repository

  def Init(): Unit = {
    val dirPath = CurrentDirPath
    Repository.initializeRepo(dirPath)
  }
}
