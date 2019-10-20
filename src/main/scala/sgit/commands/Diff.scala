package sgit.commands

import java.io.File

import sgit.utilities.FilesUtilities
import sgit.{Index, Repository}

import scala.Console.{print, println}

object Diff {

  //Function to put Index blobs contents in a map
  //@param: index:Index -> the index content
  //return : Map[String,List[String]] -> the blob path and content
  def blobsAndContent(index: Index): Map[String, List[String]] = {
    if (index.indexEntries.isEmpty) Map()
    else
      Map(
        index.indexEntries.head.path -> FilesUtilities
          .contentObject(index.indexEntries.head.sha)
          .drop(1)
      ) ++ blobsAndContent(Index(index.indexEntries.tail))
  }
  //Function to put working directory files
  //@param: files:List[File] -> list of files
  //return : Map[String,List[String]] -> files paths and contents
  def dirFilesAndContent(files: List[File]): Map[String, List[String]] = {
    if (files.isEmpty) Map()
    else
      Map(
        files.head.getAbsolutePath -> FilesUtilities.readFileContent(files.head)
      ) ++ dirFilesAndContent(files.tail)
  }

  //Function to compare between to list of lines
  //@param: l1 : List[String] -> list of lines of a content file
  //@param: l2 : List[String] -> list of lines of a content file
  //return : List[String] -> difference between l1 l2
  def compare(l1: List[String], l2: List[String]): List[String] = {
    l1 match {
      case _ if l1.isEmpty => l2
      case _ if l2.isEmpty => l1
      case _ => {
        val diff1 = l2.zipWithIndex.diff(l1.zipWithIndex).map(x => x._1)
        val diff2 = l1.zipWithIndex.diff(l2.zipWithIndex).map(x => x._1)
        val diffBis1 = diff1.diff(l1).filter(x => x != "")
        val diffBis2 = diff2.diff(l2).filter(x => x != "")

        preparePrintDiff(diffBis1, diffBis2)

      }
    }
  }
  //Assign ++ to added lines
  //@param: listLines: List[String]->list of addes lines
  //return : new List[String] with foreach ++
  def added(listLines: List[String]): List[String] = {
    if (listLines.isEmpty) List()
    else List("++" + listLines.head) ++ added(listLines.tail)
  }
  //Assign -- to deleted lines
  //@param: listLines: List[String]->list of deleted lines
  //return : new List[String] with foreach --
  def deleted(listLines: List[String]): List[String] = {
    if (listLines.isEmpty) List()
    else List("--" + listLines.head) ++ deleted(listLines.tail)
  }

  //Function to prepare the results for the print
  //@param l1: List[String] -> list of added content
  //@param l2: List[String] -> list of deleted content
  //@return : List[String] -> list of prepared modified content
  def preparePrintDiff(l1: List[String], l2: List[String]): List[String] = {
    if (l1.isEmpty && l2.isEmpty) List()
    else {
      added(l1) ++ deleted(l2)
    }
  }

  //Function to compare working directory map and index map
  //@param:m1: Map[String, List[String]] -> map of index
  //@param:m2: Map[String, List[String]] -> map of working directory
  //@return :Map[String, List[String]] ->difference between m1 m2
  def compareMaps(m1: Map[String, List[String]],
                  m2: Map[String, List[String]]): Map[String, List[String]] = {
    if (m1.isEmpty || m2.isEmpty) Map()
    else {
      val mapBlob = Map(
        m1.head._1 -> compare(m1(s"${m1.head._1}"), m2(s"${m1.head._1}"))
      )
      mapBlob ++ compareMaps(
        m1.filterKeys(_ != s"${m1.head._1}"),
        m2.filterKeys(_ != s"${m1.head._1}")
      )
    }
  }

  //Function to print the differences
  //@param: res: Map[String, List[String]] -> Each file and its modifications
  @scala.annotation.tailrec
  def differencesPrinter(res: Map[String, List[String]]): Unit = {
    if (res.isEmpty) Unit
    else if (res.head._2.isEmpty) differencesPrinter(res.tail)
    else {
      println(
        res.head._1 + " changes are \n" + res(res.head._1)
          .mkString("\n") + "\n" + res.head._2
          .count(_.contains("++")) + "++@@ Adds" + "\n" + res.head._2
          .count(_.contains("--")) + "--@@ Delete"
      )
      differencesPrinter(res.tail)
    }
  }
  //Function to account the modifications stats
  //@param: diff: Map[String, List[String]] -> difference between working dir index and stage
  @scala.annotation.tailrec
  def statDiff(diff: Map[String, List[String]]): Unit = {
    if (diff.isEmpty) Unit
    else {
      println(
        diff.head._1
          .split("/")
          .diff(Repository.get.getAbsolutePath.split("/"))
          .mkString("/") + ":\n" + diff.head._2
          .count(_.contains("--")) + "--@@ Delete\n" + diff.head._2
          .count(_.contains("++")) + "++@@ Add"
      )
      statDiff(diff.tail)
    }
  }
  //Diff command
  def diff(): Unit = {
    //Index content
    val indexFiles = Index.indexContent
    //Working diretory index
    val workingDirFile = Index.workingDirFiles
    //Tree Index
    val treeIndex = blobsAndContent(indexFiles)
    //Tree working directory
    val treeDir = dirFilesAndContent(workingDirFile)
    //differences of maps
    val differences = compareMaps(treeIndex, treeDir)
    differencesPrinter(differences)

  }
}
