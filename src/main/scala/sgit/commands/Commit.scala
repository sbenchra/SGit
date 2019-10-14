package sgit.commands

import sgit.{Index, ObjectBL, ObjectType, Tree, TreeL}

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
//Function to return deepest paths
  def profond(paths:List[List[String]],max:Int):List[List[String]]={
        if (paths.isEmpty)List()
        else if (paths.head.length.equals(max)) paths.head::profond(paths.tail,max)
        else profond(paths.tail,max)
  }
//Function to get lasts and their parents
  def lastsAndParents(paths:List[List[String]]):List[List[String]]={
    if (paths.isEmpty || paths.head.length==1) List()
    else  List(paths.head.last,paths.head.dropRight(1).last).reverse::lastsAndParents(paths.tail)
  }
//Function to get the parents
  def parents(lpaths:List[List[String]]): List[String]= {
    if (lpaths.isEmpty) List()
    else lpaths.head.head::parents(lpaths.tail)
  }
//Function to get the objects that have the same parent
  def commonParents(paths:List[List[String]],parent:String) : List[String]={
    if(parent.isEmpty || paths.isEmpty) List()
    else if (paths.head.head.equals(parent)) List(parent,paths.head.last)++commonParents(paths.tail,parent)
    else commonParents(paths.tail,parent)
  }
//Function to unify parents and children
  def union(lpaths:List[List[String]],parents:List[String]): List[List[String]]={
    if (lpaths.isEmpty) List()
    else parents.map(x=>commonParents(lpaths,x).distinct)
  }
//Function to get sha1 of file in the index
@scala.annotation.tailrec
def getHash(index:Index, name:String):String={
  if(index.indexEntries.isEmpty) ""
  else if (index.indexEntries.head.path.contains(name)) index.indexEntries.head.sha
  else getHash(Index(index.indexEntries.tail),name)
}
  //Function to get path of file in the index

  @scala.annotation.tailrec
  def getPath(index:Index, name:String):String={
    if(index.indexEntries.isEmpty) ""
    else if (index.indexEntries.head.path.contains(name)) index.indexEntries.head.path
    else getPath(Index(index.indexEntries.tail),name)
  }
//Funnction to create object
  def createObjects(data:List[String],acc:Map[String,List[TreeL]]):List[TreeL]={
    if(data.isEmpty) List()
    else if(data.last.contains("txt"))
      TreeL(ObjectType.blob,getPath(Status.indexContent,data.last),getHash(Status.indexContent,data.last))::createObjects(data.dropRight(1),acc)
    else if(acc.keysIterator.contains(s"${data.last}")) TreeL(ObjectType.tree,data.last,ObjectBL.sha(Tree(valueAcc(data.last,acc))))::createObjects(data.dropRight(1),acc)
  else createObjects(data.dropRight(1),acc)
  }
  //Function to get a value of the acc map
  def valueAcc(data:String, acc:Map[String,List[TreeL]]):List[TreeL]= {
    if (acc.keysIterator.contains(s"${data}")) acc(s"${data}")
    else List()
  }


// Append object to the accumulator
  def appendObjects(data:List[List[String]], acc:Map[String,List[TreeL]]):Map[String,List[TreeL]]={
    if (data.isEmpty) Map(""->List(TreeL(ObjectType.th,"","")))
    else Map(data.head.head->createObjects(data.head,acc))++appendObjects(data.tail,acc)
  }
//Preparing the commit by generating its sha and forming it s tree
  def commitPrepare(paths:List[List[String]]):Map[String,List[TreeL]]={

    @scala.annotation.tailrec
    def commitBis(paths:List[List[String]], acc:Map[String,List[TreeL]]):Map[String,List[TreeL]]= {
      if (paths.isEmpty) acc
      else
      {
        val deepestPaths=profond(paths,maxProfondeur(paths))
        val lastParents=lastsAndParents(deepestPaths)
        val listParents=parents(lastParents)
        val lastLayer=union(lastParents,listParents).distinct

        if (lastLayer.nonEmpty){
          val newDepest=deepestPaths.map(x=>x.dropRight(1)) //Delete the last elements
          val newPaths=paths.diff(deepestPaths)++newDepest
          commitBis(newPaths, appendObjects(lastLayer,acc)++acc)
        }
        else
        acc

      }
    }
    commitBis(paths,Map(""->List()))

  }




  def pr:Any={

  Commit.commitPrepare(fragmentAllPaths(pathsIndex(Status.indexContent)))
  }




  def commit():Unit={

    val indexContent= Status.indexContent
    val workingDir=Status.directoryContent




  }

}
