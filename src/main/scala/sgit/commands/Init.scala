package sgit.commands

import sgit.Repository

object Init {

  def RepositoryPath:String={
    System.getProperty("user.dir")
  }

  def Init() : Unit={
    val dirPath = RepositoryPath
    Repository.initializeRepo(dirPath)
  }
}
