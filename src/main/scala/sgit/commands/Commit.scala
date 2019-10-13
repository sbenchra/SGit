package sgit.commands

import sgit.{Index, ObjectType, Tree, TreeL}

import scala.annotation.tailrec
import scala.math.max
object Commit {
  // Recursive function returns the list of paths in the Index
def pathsIndex(index:Index): List[String]= {
  if (index.indexEntries.isEmpty) List()
  else index.indexEntries.head.path::pathsIndex(Index(index.indexEntries.tail))
}
  //Fragment the paths to strings
  def fragmentPath(path:List[String]):List[String]={
    if (path.isEmpty) List()
    else path.flatMap(_.split("/")).map(_.drop(0))
  }
// Fragment all index paths
  def fragmentAllPaths(paths:List[String]): List[List[String]]={
      if (paths.isEmpty) List()
      else fragmentPath(List(paths.head))::fragmentAllPaths(paths.tail)
  }
  //Recursive function to return the max length of path
  def maxProfondeur(paths:List[List[String]]):Int={
    if (paths.isEmpty) 0
    else max(paths.head.length,maxProfondeur(paths.tail))
  }

  def profond(paths:List[List[String]],max:Int):List[List[String]]={
        if (paths.isEmpty)List()
        else if (paths.head.length.equals(max)) paths.head::profond(paths.tail,max)
        else profond(paths.tail,max)
  }

  def lastsAndParents(paths:List[List[String]]):List[List[String]]={
    if (paths.isEmpty) List()
    else  List(paths.head.last,paths.head.dropRight(1).last).reverse::lastsAndParents(paths.tail)
  }

  def parents(lpaths:List[List[String]]): List[String]= {
    if (lpaths.isEmpty) List()
    else lpaths.head.head::parents(lpaths.tail)
  }

  def commonParents(paths:List[List[String]],parent:String) : List[String]={
    if(parent.isEmpty || paths.isEmpty) List()
    else if (paths.head.head.equals(parent)) List(parent,paths.head.last)++commonParents(paths.tail,parent)
    else commonParents(paths.tail,parent)
  }

  def union(lpaths:List[List[String]],parents:List[String]): List[List[String]]={
    if (lpaths.isEmpty) List()
    else parents.map(x=>commonParents(lpaths,x).distinct)
  }

def getHash(index:Index,name:String):String={
  if(index.indexEntries.isEmpty) ""
  else if (index.indexEntries.head.path.contains(name)) index.indexEntries.head.sha
  else getHash(Index(index.indexEntries.tail),name)
}

  def getPath(index:Index,name:String):String={
    if(index.indexEntries.isEmpty) ""
    else if (index.indexEntries.head.path.contains(name)) index.indexEntries.head.path
    else getHash(Index(index.indexEntries.tail),name)
  }


  def formObjects(path:List[String]):List[TreeL]={
    if (path.isEmpty) List()
    else if (path.last.contains("txt")) TreeL(ObjectType.blob,getPath(Status.indexContent,path.last),getHash(Status.indexContent,path.last))::formObjects(path.dropRight(1))
    else

  }

/*

def commit(lPaths:List[List[String]]): Unit= {

  val lasts = lasts(lPaths)
}



 */

  def pr:Any={

  union (lastsAndParents(fragmentAllPaths(pathsIndex(Status.indexContent))),parents( lastsAndParents(fragmentAllPaths(pathsIndex(Status.indexContent))))).distinct
  }




  def commit():Unit={

    val indexContent= Status.indexContent
    val workingDir=Status.directoryContent




  }

}
