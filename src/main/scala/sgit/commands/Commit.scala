

package sgit.commands

import java.io.File
import java.time.LocalDate
import sgit.utilities.FilesUtilities
import sgit.{Index, ObjectBL, ObjectType, Repository, Tree, TreeL}

import scala.math.max
object Commit {
  //Returns the head file of the repository
  def headFilePath:String={
    Repository.get.getAbsolutePath+"/.sgit/HEAD"
  }

    //Recursive function returns the list of paths in the Index
    //@param: index: Index -> Index of sgit repository
    //Return : List[String] -> List of paths
   def pathsIndex(index:Index): List[String]= {
    if (index.indexEntries.isEmpty) List()
    else index.indexEntries.head.path::pathsIndex(Index(index.indexEntries.tail))
  }

  // Split list of paths
  //@param:path:List[String] -> list of paths
  //Return : List[List[String]] -> Lists of list of each path elements
    def fragmentAllPaths(paths:List[String]): List[List[String]]={
        if (paths.isEmpty) List()
        else List(paths.flatMap(_.split("/")).map(_.drop(0)))++fragmentAllPaths(paths.tail)
    }
  //Recursive function to return the max length of path
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@Return:  The max length of a path in the list
    def maxLength(paths:List[List[String]]):Int={
      if (paths.isEmpty) 0
      else max(paths.head.length,maxLength(paths.tail))
    }

  //Function to return deepest paths
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@Return : the deepest path
    def deepestPath(paths:List[List[String]], max:Int):List[List[String]]={
          if (paths.isEmpty)List()
          else if (paths.head.length.equals(max)) paths.head::deepestPath(paths.tail,max)
          else deepestPath(paths.tail,max)
    }
  //Function to get lasts and their parents
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //Return: List[List[String]] -> last elements of the paths and their parents
    def lastsAndParents(paths:List[List[String]]):List[List[String]]={
      if (paths.isEmpty || paths.head.length==1) List()
      else  List(paths.head.last,paths.head.dropRight(1).last).reverse::lastsAndParents(paths.tail)
    }

  //Function to get the objects that have the same parent
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@return : List[string] -> elements having the same parent
  def commonParents(paths:List[List[String]],parent:String) : List[String]={
      if(parent.isEmpty || paths.isEmpty) List()
      else if (paths.head.head.equals(parent)) List(parent,paths.head.last)++commonParents(paths.tail,parent)
      else commonParents(paths.tail,parent)
    }
  //Function to unify parents and children
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@param: parents: List[String] -> list of files parents
  //Return : List[List[String]] -> each parent with its children files
  def union(lPaths:List[List[String]], parents:List[String]): List[List[String]]={
      if (lPaths.isEmpty) List()
      else parents.map(x=>commonParents(lPaths,x).distinct)
    }
  //Function to get sha1 of file in the index
  //@param: index:Index -> index file of the repository
  //@param: name:String -> file name
  //@return: String -> sha1 of the file
  @scala.annotation.tailrec
  def getHash(index:Index, name:String):String={
    if(index.indexEntries.isEmpty) ""
    else if (index.indexEntries.head.path.contains(name)) index.indexEntries.head.sha
    else getHash(Index(index.indexEntries.tail),name)
  }
    //Function to get path of file in the index
    //@param: index:Index -> index file of the repository
    //@param: name:String -> file name
    //@return: String -> path of the file
    @scala.annotation.tailrec
    def getPath(index:Index, name:String):String={
      if(index.indexEntries.isEmpty) ""
      else if (index.indexEntries.head.path.contains(name)) index.indexEntries.head.path
      else getPath(Index(index.indexEntries.tail),name)
    }
  //Funnction to create object
  //@param: index:Index -> index file of the repository
  //@param: data:List[String] -> file name
  //@return: String -> sha1 of the file
    def createObjects(index:Index,data:List[String],acc:Map[String,List[TreeL]]):List[TreeL]={
      if(data.isEmpty) List()
      else if(data.last.contains("txt"))
        TreeL(ObjectType.blob,getPath(index,data.last),getHash(index,data.last))::createObjects(index,data.dropRight(1),acc)
      else if(acc.keysIterator.contains(s"${data.last}")) TreeL(ObjectType.tree,data.last,ObjectBL.sha(Tree(valueAcc(data.last,acc))))::createObjects(index,data.dropRight(1),acc)
      else createObjects(index,data.dropRight(1),acc)
    }
    //Function to get a value of the acc map
    def valueAcc(data:String, acc:Map[String,List[TreeL]]):List[TreeL]= {
      if (acc.keysIterator.contains(s"${data}")) acc(s"${data}")
      else List()
    }


  // Append object to the accumulator
    def appendObjects(index:Index,data:List[List[String]], acc:Map[String,List[TreeL]]):Map[String,List[TreeL]]={
      if (data.isEmpty) Map()
      else Map(data.head.head->createObjects(index,data.head,acc))++appendObjects(index,data.tail,acc)
    }
  //Preparing the commit by generating its sha and forming it s tree
    def commitPrepare(index:Index,paths:List[List[String]]):Map[String,List[TreeL]]={

      @scala.annotation.tailrec
      def commitPrepareBis(index:Index,paths:List[List[String]], acc:Map[String,List[TreeL]]):Map[String,List[TreeL]]= {
        if (paths.isEmpty) acc
        else
        {
          val deepestPaths=deepestPath(paths,maxLength(paths))
          val lastAParents=lastsAndParents(deepestPaths)
          val listParents=lastAParents.map(_.head)
          val lastLayer=union(lastAParents,listParents).distinct

          if (lastLayer.nonEmpty){
            val newDepest=deepestPaths.map(x=>x.dropRight(1)) //Delete the last elements
            val newPaths=paths.diff(deepestPaths)++newDepest
            commitPrepareBis(index,newPaths, appendObjects(index,lastLayer,acc)++acc)
          }
          else
          acc

        }
      }
      val b=Repository.getWorkingDirPath(Init.CureentFile).split("/")
      commitPrepareBis(index,paths.map(x=>List(".")++x.diff(b)),Map())

    }


  //Check if all files are staged before commit
  @scala.annotation.tailrec
  def allFileAreStaged(files:List[File], index:Index):Boolean= {
      if (files.isEmpty) true
      else {
        Index.fieldInIndex(files.head.getAbsolutePath, index) && allFileAreStaged(files.tail, index)

      }
    }

      @scala.annotation.tailrec
      def allStagedExists(index: Index): Boolean={
        if (index.indexEntries.isEmpty) true
        else new File(index.indexEntries.head.path).exists() && allStagedExists(Index(index.indexEntries.tail))
      }


    //The index commit Map
    def commitMapF(index:Index):Map[String,List[TreeL]]={
      val fragmentedPaths= fragmentAllPaths(pathsIndex(Index.indexContent))
      commitPrepare(index,fragmentedPaths)

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
      val commitMap=commitMapF(Index.indexContent)
      // The working directory commit tree
      val commitMapDir=commitMapF(Index.directoryContent)
      val commitEntries= if(commitMap.keysIterator.exists(_.contains("."))) commitMap(".")
      else { commitMap("")}
      val commitEntriesDir=if(commitMapDir.keysIterator.exists(_.contains("."))) commitMapDir(".")
      else { commitMapDir("")}
      val (branch: File, lastCommitId: String) = lastCommit
      //Index Commit
      val commitObject=sgit.Commit("Soufiane","Soufiane",LocalDate.now(),msg,Tree(commitEntries),lastCommitId)
      //Workind directory commit
      val fakeCommit=sgit.Commit("Soufiane","Soufiane",LocalDate.now(),msg,Tree(commitEntriesDir),lastCommitId)
      commitObject match {
        case _ if allStagedExists(Index.indexContent)
          && allFileAreStaged(Index.workingDirFiles,Index.indexContent)
          &&ObjectBL.sha(commitObject)!=lastCommitId &&
          ObjectBL.sha(commitObject).equals(ObjectBL.sha(fakeCommit))&&
          Index.indexContentToIndex(FilesUtilities.indexContentBis).diff(Index.workingDirBlobs(Index.workingDirFiles)).isEmpty
        =>
        {
          writeTrees(commitMap)
          ObjectBL.addObject(commitObject)
          FilesUtilities.writeCommitMessage(msg)
          val sha=ObjectBL.sha(commitObject)
          FilesUtilities.changeBranchSha(sha,branch)
          FilesUtilities.writeInFile(Log.logFile(),List("Commit:"+ObjectBL.sha(commitObject)+"\n","Author:"+commitObject.authorName+"\n","Date:"+commitObject.commitDate+"\n","Parent:"+lastCommitId,"\n","Message:"+msg,"\n"))
        }
        case _ if ObjectBL.sha(commitObject).equals(ObjectBL.sha(fakeCommit)) && Index.indexContent.indexEntries.nonEmpty => println("Everything is up to date")
        case _ => Status.status()
      }



    }

    def lastCommit: (File, String) = {
      val headFile = new File(headFilePath)
      //Currrent Branch
      val currentBranch = FilesUtilities.readFileContent(headFile).head.substring(5)
      val branch = new File(Repository.get.getAbsolutePath+"/.sgit/"+currentBranch)
      //Last commit Id
      val lastCommitId = {
        if (branch.exists()) FilesUtilities.readFileContent(branch).head
        else "19011995"
      }
      (branch, lastCommitId)
    }
  }
