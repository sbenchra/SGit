package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities
import sgit.{Index, ObjectBL, ObjectType, Repository, Tree, TreeL}

import scala.math.max
object Commit {
  def headFilePath:String={
    Init.RepositoryPath+"/.sgit/HEAD"
  }
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
    def commitPrepareBis(paths:List[List[String]], acc:Map[String,List[TreeL]]):Map[String,List[TreeL]]= {
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
          commitPrepareBis(newPaths, appendObjects(lastLayer,acc)++acc)
        }
        else
        acc

      }
    }
    commitPrepareBis(paths,Map(""->List()))

  }


//Check if all files are staged before commit
@scala.annotation.tailrec
def allFileAreStaged(files:List[File], index:Index):Boolean= {
    if (files.isEmpty) true
    else {
      Index.fieldInIndex(files.head.getPath, index) && allFileAreStaged(files.tail, index)

    }
  }

    @scala.annotation.tailrec
    def allStagedExists(index: Index): Boolean={
      if (index.indexEntries.isEmpty) true
      else new File(index.indexEntries.head.path).exists() && allStagedExists(Index(index.indexEntries.tail))
    }

  def indexContent:Index= Status.indexContent
  def lFilesBis:List[File]= FilesUtilities.filesOfListFiles(List(new File("./soufiane")))

  //The index commit Map
  def commitMapF:Map[String,List[TreeL]]={
    val fragmentedPaths= fragmentAllPaths(pathsIndex(indexContent))
    commitPrepare(fragmentedPaths)

  }
  //Construct the  working directory
  def wDirMapF:Map[String,List[TreeL]]={
    //Paths of files of the working directory
    //get Dir Paths
    val filesPathDir=lFilesBis.map(_.getPath)
    val fragmentedDirPaths =fragmentAllPaths(filesPathDir)
    commitPrepare(fragmentedDirPaths)
  }
  @scala.annotation.tailrec
  def writeTrees(CommitEntries:Map[String,List[TreeL]]):Unit={
    val setCommitEntries=CommitEntries.filterKeys(_!="").toSeq
    if(setCommitEntries.isEmpty) Unit
    else {
      ObjectBL.addObject(Tree(setCommitEntries.head._2))
      writeTrees(CommitEntries-setCommitEntries.head._1)}
  }


  def commit(msg:String):Unit={

    //Find the index commit tree
    val commitMap=commitMapF
    // The working directory commit tree
    val commitMapDir=wDirMapF
    val commitEntries= if(commitMap.keysIterator.exists(_.contains("."))) commitMap(".")
    else { commitMap("")}
    val commitEntriesDir=if(commitMapDir.keysIterator.exists(_.contains("."))) commitMapDir(".")
    else { commitMapDir("")}

    val headFile=new File(headFilePath)
    //Currrent Branch
    val currentBranch=FilesUtilities.readFileContent(headFile).split(" ")
    val branch=new File(Init.RepositoryPath+"/.sgit/"+currentBranch(1))
    //Last commit Id
    val lastCommitId= {if (branch.exists()) FilesUtilities.readFileContent(branch)
                    else "19011995" }
    //Index Commit
    val commitObject=sgit.Commit("","","","","",msg,Tree(commitEntries),lastCommitId)
    //Workind directory commit
    val fakeCommit=sgit.Commit("","","","","",msg,Tree(commitEntriesDir),lastCommitId)

    commitObject match {
      case _ if (allStagedExists(indexContent)&& allFileAreStaged(lFilesBis,indexContent) &&ObjectBL.sha(commitObject)!=lastCommitId && ObjectBL.sha(commitObject).equals(ObjectBL.sha(fakeCommit) ))=>{
        writeTrees(commitMap)
        ObjectBL.addObject(commitObject)
        FilesUtilities.writeCommitMessage(msg)
        FilesUtilities.changeBranchSha(ObjectBL.sha(commitObject),branch)
        FilesUtilities.writeInFile(Log.logFile(),List("Commit:"+ObjectBL.sha(commitObject)+"\n","Author:"+commitObject.authorName+"\n","Date:"+commitObject.commitDate+"\n","Parent:"+lastCommitId,"\n","Message:"+msg,"\n"))
      }
      case _ if (ObjectBL.sha(commitObject).equals(ObjectBL.sha(fakeCommit)) && indexContent.indexEntries.nonEmpty )=> print("Everything is up to date")
      case _ => Status.status()
    }



  }

}
