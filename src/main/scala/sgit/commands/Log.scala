package sgit.commands
import java.io.File

import sgit.utilities.FilesUtilities
import sgit.{Index, IndexEntry, ObjectBL, Repository}

object Log {

  //Log file
  //@return : file-> repository log file
  def logFile(): File = new File(Repository.get.getAbsolutePath + "/.sgit/logs")

  //Log content
  //@return : List[String] -> log file content
  def logContent: List[String] = {
    FilesUtilities.readFileContent(logFile())
  }

  //Log content as string splited by \n
  //@return: Array[String] -> log file content
  def logContentArray: Array[String] = { logContent.toArray }

  //blob contents of all comits
  //@param: commitsAndParents: Map[String, String] -> Map of commit and its parent
  //@return: Map[String, String] -> each commit with its blobs and their contents
  private def blobsContents(commitsAndParents: Map[String, String]) = {
    blobIndexContents(commitIndex(commitsAndParents))
  }

  //Commits and there parents return map
  //@return: Map[String, String] -> A map of commit and its parents
  private def commitParent: Map[String, String] = {
    val commitsAndParents = commitAndParent(logContentArray)
    commitsAndParents
  }

  //Function to exctract the commits and parents
  //@param: logContentA:Array[String] -> log content
  //@return : Map{String,String] -> Commits and parents
  def commitAndParent(logContentA: Array[String]): Map[String, String] = {
    logContentA match {
      case _ if logContentA.isEmpty => Map()
      case _ if logContentA(3).contains("19011995") =>
        commitAndParent(logContentA.drop(5))
      case _ =>
        Map(logContentA.head.diff("Commit:") -> logContentA(3).diff("Parent:")) ++ commitAndParent(
          logContentA.drop(5)
        )
    }
  }

  //Tranform blob from the object directory to map
  def blobsToMap(list: List[String]): Map[String, String] = {
    if (list.isEmpty) Map()
    else {
      val blob = list.head.split(" ").drop(1)
      Map(blob.head -> blob.last) ++ blobsToMap(list.tail)
    }
  }

  //Function to list all the trees of a given commit
  def commitTree(trees: List[String]): List[String] = {
    if (trees.isEmpty) List()
    else {
      val treesBis =
        ObjectBL.extractTree(FilesUtilities.contentObject(trees.head))
      treesBis ++ commitTree(trees.tail) ++ commitTree(treesBis)
    }
  }

  //Function to list all the blobs of a given trees
  def blobsCommit(trees: List[String]): List[String] = {
    if (trees.isEmpty) List()
    else {
      val blobs = ObjectBL.extractBlob(FilesUtilities.contentObject(trees.head))
      blobs ++ blobsCommit(trees.tail)
    }
  }

  //Function that returns the blobs of a given commit path->sha
  def commitBlobs(commitId: String): Map[String, String] = {
    val commitContent = FilesUtilities.contentObject(commitId)
    val treeCommit = ObjectBL.extractTree(commitContent)
    blobsToMap(blobsCommit(commitTree(treeCommit)))
  }

//Function to construct all commits blobs
  def constructsCommitMap(
    commits: Map[String, String]
  ): Map[String, Map[String, String]] = {
    if (commits.isEmpty) Map()
    else {
      Map(commits.head._1 -> commitBlobs(commits.head._1)) ++ Map(
        commits.head._2 -> commitBlobs(commits.head._2)
      ) ++ constructsCommitMap(commits.tail)
    }

  }

  //function to transform a map to a list of index entries
  def mapToIndex(map: Map[String, String]): List[IndexEntry] = {
    if (map.isEmpty) List()
    else IndexEntry(map.head._1, map.head._2) :: mapToIndex(map.tail)
  }

  //Construct the index of each commit
  def constructsIndex(
    commits: Map[String, Map[String, String]]
  ): Map[String, Index] = {
    if (commits.isEmpty) Map()
    else
      Map(commits.head._1 -> Index(mapToIndex(commits.head._2))) ++ constructsIndex(
        commits.tail
      )
  }

  def commitIndex(
    commitsAndParents: Map[String, String]
  ): Map[String, Index] = {
    if (commitsAndParents.isEmpty) Map()
    else {
      val commitMap = constructsCommitMap(commitsAndParents)
      constructsIndex(commitMap)
    }
  }

  //Function to return the commit and its blobs and their content
  def blobIndexContents(
    commitIndex: Map[String, Index]
  ): Map[String, Map[String, List[String]]] = {
    if (commitIndex.isEmpty) Map()
    else
      Map(commitIndex.head._1 -> Diff.blobsAndContent(commitIndex.head._2)) ++ blobIndexContents(
        commitIndex.tail
      )
  }

  //Function to check de the difference between two blobs
  //@param : blobs1: Map[String, String] -> parent blobs
  //@param : blobs2: Map[String, String] -> commit blobs
  @scala.annotation.tailrec
  def checkDiff(blobs1: Map[String, String],
                blobs2: Map[String, String]): Unit = {
    if (blobs1.isEmpty || blobs2.isEmpty) Unit
    else if (!blobs2.exists(_._1 == blobs1.head._1) && !blobs2.exists(
               _._2 == blobs1.head._2
             )) {
      print("\n" + blobs1.head._1 + " is deleted")
      checkDiff(blobs1.tail, blobs2)
    } else if (!blobs1.exists(_._1 == blobs2.head._1) && !blobs1.exists(
                 _._2 == blobs2.head._2
               )) {
      print("\n" + blobs1.head._1 + " is added")
      checkDiff(blobs1, blobs2.tail)

    } else if (blobs1.exists(_._1 == blobs2.head._1) && !blobs1.exists(
                 _._2 == blobs2.head._2
               )) {
      print("\n" + blobs1.head._1 + " is modified")
      checkDiff(blobs1, blobs2.tail)
    }

  }

  //Recursive function to print de the differences of blobs between a commit blobs and its parent
  //@param: commitsAndParents: Map[String, String] -> commit ->parent
  //@param: blobs: Map[String, Map[String, List[String]]] -> commit-> (blob->content of the blob)
  @scala.annotation.tailrec
  def logPBis(commitsAndParents: Map[String, String],
              blobs: Map[String, Map[String, List[String]]]): Unit = {
    if (commitsAndParents.isEmpty) Unit
    else {
      val parentBlobs = blobs(commitsAndParents.head._2)
      val commitBlobs = blobs(commitsAndParents.head._1)
      val commitTree2 = commitTree(List(commitsAndParents.head._2))
      val commitTree1 = commitTree(List(commitsAndParents.head._1))
      val blobs2 = blobsCommit(commitTree2)
      val blobs1 = blobsCommit(commitTree1)
      val mapBlobs2 = blobsToMap(blobs2)
      val mapBlobs1 = blobsToMap(blobs1)
      print(
        "\nCommit: " + commitsAndParents.head._1 + "\n" + "Parent: " + commitsAndParents.head._2 + "\n"
      )
      Diff.differencesPrinter(Diff.compareMaps(parentBlobs, commitBlobs))
      checkDiff(mapBlobs1, mapBlobs2)
      logPBis(commitsAndParents.tail, blobs)
    }
  }
  // A recursive function to display log stats
  //@param: commitsAndParents: Map[String, String] -> commit ->parent
  //@param: blobs: Map[String, Map[String, List[String]]] -> commit-> (blob->content of the blob)
  @scala.annotation.tailrec
  def logStatBis(commitsAndParents: Map[String, String],
                 blobs: Map[String, Map[String, List[String]]]): Unit = {
    if (commitsAndParents.isEmpty) Unit
    else {
      val commitBlobs = blobs(commitsAndParents.head._1)
      val parentBlobs = blobs(commitsAndParents.head._2)
      println(
        "Parent: " + commitsAndParents.head._1 + " Commit: " + commitsAndParents.head._2
      )
      Diff.statDiff(Diff.compareMaps(parentBlobs, commitBlobs))
      logStatBis(commitsAndParents.tail, blobs)
    }
  }

  //function to start the command log
  def log(): Unit = {
    print(logContent.mkString("\n"))

  }

  //Function to start the Log -p Command
  def logP(): Unit = {
    val commitsAndParents: _root_.scala.Predef.Map[_root_.scala.Predef.String,
                                                   _root_.scala.Predef.String] =
      commitParent

    logPBis(commitsAndParents, blobsContents(commitsAndParents))

  }

//Method to start the log -stat command
  def logStat(): Unit = {
    logStatBis(commitParent, blobsContents(commitParent))
  }

}
