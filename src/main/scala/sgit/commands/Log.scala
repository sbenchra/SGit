package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities


object Log {
  def logFile(): File=new File(Init.RepositoryPath+"/.sgit/logs")
    //Log content
def logContent:String={
  FilesUtilities.readFileContent(logFile())
}
  //Log content as string splited by \n
  def logContentArray: Array[String] ={logContent.split("\n")}


//Function to return the commits with the parents in a map
  def commitAndParent(logContentA:Array[String]):Map[String,String]={
    if (logContentA.isEmpty) Map()
    else if (logContentA.head.contains("Commit")) Map(logContentA.head.diff("Commit:")->logContentA(logContentA.indexOf(logContentA.head)+3).diff("Parent:"))++commitAndParent(logContentA.tail)
    else commitAndParent(logContentA.tail)

  }


  def log():Unit={

    print(logContent)

  }

  def logP():Unit={

  }
}
