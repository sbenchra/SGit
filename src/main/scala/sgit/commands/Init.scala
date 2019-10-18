package sgit.commands

import java.io.File

import sgit.Repository

object Init {

  def CurrentDirPath:String={
    System.getProperty("user.dir")
  }
  def CureentFile:File={
    new File(CurrentDirPath)
  }
//Initialize the repository

  def Init() : Unit={
    val dirPath = CurrentDirPath
    Repository.initializeRepo(dirPath)
  }
}
