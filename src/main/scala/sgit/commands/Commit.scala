package sgit.commands

import java.io.File
import java.time.LocalDate

import sgit.utilities.FilesUtilities
import sgit.{Index, _}

import scala.math.max
object Commit {
  //Returns the head file of the repository
  def headFilePath: String = {
    Repository.get.getAbsolutePath + "/.sgit/HEAD"
  }

  //Get the current branch and the last commit Id
  def lastCommitBranch: (File, String) = {
    val headFile = new File(headFilePath)
    //Currrent Branch
    val currentBranch =
      if (FilesUtilities.readFileContent(headFile).nonEmpty)
        FilesUtilities.readFileContent(headFile).head.substring(5)
      else ""
    val branch = new File(
      Repository.get.getAbsolutePath + "/.sgit/" + currentBranch
    )
    //Last commit Id

    val lastCommitId = {
      if (branch.exists()) FilesUtilities.readFileContent(branch).head
      else "19011995"
    }
    (branch, lastCommitId)
  }

  //Recursive function returns the list of paths in the Index
  //@param: index: Index -> Index of sgit repository
  //Return : List[String] -> List of paths
  def pathsIndex(index: Index): List[String] = {
    if (index.indexEntries.isEmpty) List()
    else
      index.indexEntries.head.path :: pathsIndex(Index(index.indexEntries.tail))
  }

  def fragmentPath(path: List[String]): List[String] = {
    if (path.isEmpty) List()
    else path.flatMap(_.split("/")).map(_.drop(0))
  }

  // Split list of paths
  //@param:path:List[String] -> list of paths
  //Return : List[List[String]] -> Lists of list of each path elements
  def fragmentAllPaths(paths: List[String]): List[List[String]] = {
    if (paths.isEmpty) List()
    else fragmentPath(List(paths.head)) :: fragmentAllPaths(paths.tail)
  }
  //Recursive function to return the max length of path
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@Return:  The max length of a path in the list
  def maxLength(paths: List[List[String]]): Int = {
    if (paths.isEmpty) 0
    else max(paths.head.length, maxLength(paths.tail))
  }

  //Function to return deepest paths
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@Return : the deepest path
  def deepestPath(paths: List[List[String]], max: Int): List[List[String]] = {
    paths match {
      case _ if paths.isEmpty => List()
      case _ if paths.head.length.equals(max) =>
        paths.head :: deepestPath(paths.tail, max)
      case _ => deepestPath(paths.tail, max)
    }
  }
  //Function to get lasts and their parents
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //Return: List[List[String]] -> last elements of the paths and their parents
  def lastsAndParents(paths: List[List[String]]): List[List[String]] = {
    if (paths.isEmpty || paths.head.length == 1) List()
    else
      List(paths.head.last, paths.head.dropRight(1).last).reverse :: lastsAndParents(
        paths.tail
      )
  }

  //Function to get the objects that have the same parent
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@return : List[string] -> elements having the same parent
  def commonParents(paths: List[List[String]], parent: String): List[String] = {
    paths match {
      case _ if parent.isEmpty || paths.isEmpty => List()
      case _ if paths.head.head.equals(parent) =>
        List(parent, paths.head.last) ++ commonParents(paths.tail, parent)
      case _ => commonParents(paths.tail, parent)
    }
  }
  //Function to unify parents and children
  //@Param: paths : List[List[String]] -> list of list of paths elements
  //@param: parents: List[String] -> list of files parents
  //Return : List[List[String]] -> each parent with its children files
  def union(lPaths: List[List[String]],
            parents: List[String]): List[List[String]] = {
    if (lPaths.isEmpty) List()
    else parents.map(x => commonParents(lPaths, x).distinct)
  }
  //Function to get sha1 of file in the index
  //@param: index:Index -> index file of the repository
  //@param: name:String -> file name
  //@return: String -> sha1 of the file
  @scala.annotation.tailrec
  def getHash(index: Index, name: String): String = {
    index match {
      case _ if index.indexEntries.isEmpty => ""
      case _ if index.indexEntries.head.path.contains(name) =>
        index.indexEntries.head.sha
      case _ => getHash(Index(index.indexEntries.tail), name)
    }

  }
  //Function to get path of file in the index
  //@param: index:Index -> index file of the repository
  //@param: name:String -> file name
  //@return: String -> path of the file
  @scala.annotation.tailrec
  def getPath(index: Index, name: String): String = {
    index match {
      case _ if index.indexEntries.isEmpty => ""
      case _ if index.indexEntries.head.path.contains(name) =>
        index.indexEntries.head.path
      case _ => getPath(Index(index.indexEntries.tail), name)
    }
  }
  //Function to create objects
  //@param: index:Index -> index file of the repository
  // @param : trees:Map[String,List[TreeL]] -> trees already created
  // @param: data:List[String] -> Objects to create
  //@return: List[TreeL] -> List of tree entries of new objects
  def createObjects(index: Index,
                    data: List[String],
                    trees: Map[String, List[TreeL]]): List[TreeL] = {
    index match {
      case _ if data.isEmpty => List()
      case _ if isblob(data.last,Index.indexContent) =>
        TreeL(
          ObjectType.blob,
          getPath(index, data.last),
          getHash(index, data.last)
        ) :: createObjects(index, data.dropRight(1), trees)
      case _ if trees.keysIterator.contains(s"${data.last}") =>
        val valueT = valueTree(data.last, trees)
        TreeL(ObjectType.tree, data.last, ObjectBL.sha(Tree(valueT))) :: createObjects(
          index,
          data.dropRight(1),
          trees
        )
      case _ => createObjects(index, data.dropRight(1), trees)
    }
  }

  @scala.annotation.tailrec
  def isblob(file:String, index:Index): Boolean= {
    if(index.indexEntries.isEmpty) false
    else if( index.indexEntries.head.path.split("/").last.equals(file)) true
    else isblob(file,Index(index.indexEntries.tail))
  }
  //Value of data in tree
  // @param : trees:Map[String,List[TreeL]] -> tree and its entries
  // @param: data -> object name
  //@return: List[TreeL] -> List of tree entries of object
  def valueTree(data: String, trees: Map[String, List[TreeL]]): List[TreeL] = {
    if (trees.keysIterator.contains(s"$data")) trees(s"$data")
    else List()
  }
  // Append object to the trees already created
  //@param: index:Index -> index file of the repository
  // @param : trees:Map[String,List[TreeL]] -> trees already created
  // @param: data:List[List[String]] -> Objects to create
  //@return: Map[String,List[TreeL]] -> Trees created after inspecting data
  def appendObjects(
    index: Index,
    data: List[List[String]],
    trees: Map[String, List[TreeL]]
  ): Map[String, List[TreeL]] = {
    if (data.isEmpty) Map()
    else
      Map(data.head.head -> createObjects(index, data.head, trees)) ++ appendObjects(
        index,
        data.tail,
        trees
      )
  }

  //Preparing the commit by generating its sha and forming it s tree
  def commitPrepare(index: Index,
                    paths: List[List[String]]): Map[String, List[TreeL]] = {
    @scala.annotation.tailrec
    def commitPrepareBis(
      index: Index,
      paths: List[List[String]],
      acc: Map[String, List[TreeL]]
    ): Map[String, List[TreeL]] = {
      if (paths.isEmpty) acc
      else {

        val deepestPaths = deepestPath(paths, maxLength(paths))
        val lastAParents = lastsAndParents(deepestPaths)
        val listParents = lastAParents.map(_.head)
        val lastLayer = union(lastAParents, listParents).distinct

        if (lastLayer.nonEmpty) {

          val newDepest = deepestPaths.map(x => x.dropRight(1)) //Delete the last elements
          val newPaths = paths.diff(deepestPaths) ++ newDepest
          commitPrepareBis(
            index,
            newPaths,
            appendObjects(index, lastLayer, acc) ++ acc
          )
        } else
          acc
      }
    }
    val fullPath = Repository.getWorkingDirPath(Init.CureentFile).split("/")
    commitPrepareBis(
      index,
      paths.map(x => List(".") ++ x.diff(fullPath)),
      Map()
    )
  }

  //Check if all files are staged before commit
  @scala.annotation.tailrec
  def allFileAreStaged(files: List[File], index: Index): Boolean = {
    if (files.isEmpty) true
    else {
      Index.fieldInIndex(files.head.getAbsolutePath, index) && allFileAreStaged(
        files.tail,
        index
      )

    }
  }
  //Check if a file was deleted from the working directory
  @scala.annotation.tailrec
  def allStagedExists(index: Index): Boolean = {
    if (index.indexEntries.isEmpty) true
    else
      new File(index.indexEntries.head.path).exists() && allStagedExists(
        Index(index.indexEntries.tail)
      )
  }

  //The index commit tree
  //@param: index content -> the index content
  //return : Map of the commit tree
  def commitTreeF(index: Index): Map[String, List[TreeL]] = {
    val pathsI = pathsIndex(Index.indexContent)
    val fragmentedPaths = fragmentAllPaths(pathsI)
    commitPrepare(index, fragmentedPaths)
  }

  //create files of objects of the commit map
  //@param:commitEntries:Map[String,List[TreeL]]-> CommitMap
  @scala.annotation.tailrec
  def writeTrees(commitEntries: Map[String, List[TreeL]]): Unit = {
    val setCommitEntries = commitEntries.filterKeys(_ != "").toSeq
    if (setCommitEntries.isEmpty) Unit
    else {
      ObjectBL.addObject(Tree(setCommitEntries.head._2))
      writeTrees(commitEntries - setCommitEntries.head._1)
    }
  }

  def commit(msg: String): Unit = {

    //Find the index commit tree
    val commitMap = commitTreeF(Index.indexContent)
    // The working directory commit tree
    val commitMapDir = commitTreeF(Index.directoryContent)

    val commitEntries =
      if (commitMap.keysIterator.exists(_.contains("."))) commitMap(".")
      else { commitMap("") }
    val commitEntriesDir =
      if (commitMapDir.keysIterator.exists(_.contains("."))) commitMapDir(".")
      else { commitMapDir("") }

    val (branch: File, lastCommitId: String) = lastCommitBranch
    //Index Commit
    val commitObject = sgit.Commit(
      "Soufiane",
      "Soufiane",
      LocalDate.now(),
      msg,
      Tree(commitEntries),
      lastCommitId
    )
    //Workind directory commit
    val fakeCommit = sgit.Commit(
      "Soufiane",
      "Soufiane",
      LocalDate.now(),
      msg,
      Tree(commitEntriesDir),
      lastCommitId
    )

    commitObject match {
      //If all the files are staged and no file was deleted
      //If the new commit is different than the last one
      case _
          if allStagedExists(Index.indexContent)
            && allFileAreStaged(Index.workingDirFiles.filter(!_.getAbsolutePath.contains(".sgit")), Index.indexContent)
            && ObjectBL.sha(commitObject) != lastCommitId &&
            ObjectBL.sha(commitObject).equals(ObjectBL.sha(fakeCommit))=> {
        writeTrees(commitMap)
        ObjectBL.addObject(commitObject)
        FilesUtilities.writeCommitMessage(msg)
        val sha = ObjectBL.sha(commitObject)
        FilesUtilities.changeBranchSha(sha, branch)
        FilesUtilities.writeInFile(
          Log.logFile(),
          List(
            "Commit:" + ObjectBL.sha(commitObject) + "\n",
            "Author:" + commitObject.authorName + "\n",
            "Date:" + commitObject.commitDate + "\n",
            "Parent:" + lastCommitId,
            "\n",
            "Message:" + msg,
            "\n"
          )
        )
      }
      case _
        if
          Index
    .stageContentToIndexEntries(FilesUtilities.indexContentBis)
    .diff(Index.workingDirIndex(Index.workingDirFiles))
    .nonEmpty  =>
        println("Stage tracked files before commit")
      //If the new commit is the same as the last one
      case _
          if ObjectBL
            .sha(commitObject)
            .equals(ObjectBL.sha(fakeCommit)) && Index.indexContent.indexEntries.nonEmpty =>
        println("Everything is up to date")
      case _ => Status.status()
    }

  }

}
