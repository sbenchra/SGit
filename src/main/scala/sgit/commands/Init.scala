package sgit.commands

import sgit.Repository

object Init {

  def RepositoryPath:String={
    System.getProperty("user.dir")
  }
//Initialize the repository

  def Init() : Unit={
    val dirPath = RepositoryPath
    Repository.initializeRepo(dirPath)
  }
}
