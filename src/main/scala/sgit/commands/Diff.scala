package sgit.commands

import java.io.File

import sgit.Index
import sgit.utilities.FilesUtilities

object Diff {

//Function to put Index blobs contents in a map
  def blobsAndContent(index:Index):Map[String,List[String]]={
    if (index.indexEntries.isEmpty) Map(""->List())
    else Map(index.indexEntries.head.path->FilesUtilities.contentBlob(index.indexEntries.head.sha).drop(1))++ blobsAndContent(Index(index.indexEntries.tail))
  }
  //Function to put working directory files
  def dirFilesAndContent(files:List[File]):Map[String,List[String]]={
    if (files.isEmpty) Map(""->List())
    else Map(files.head.getPath->FilesUtilities.readFileContent(files.head).split("\n").toList) ++ dirFilesAndContent(files.tail)
  }

  //Function to compare between to list of lines
  def compare(l1:List[String],l2:List[String]): List[String]= {
    if (l1.isEmpty) l2
    else if (l2.isEmpty) l1
    else {
      printDiff( l2.diff(l1),l1.diff(l2))
    }
  }
  //Function to prepare the results for the print
  def printDiff(l1:List[String],l2:List[String]):List[String]={
    if(l1.isEmpty) l2
    else if (l2.isEmpty) l1
    else{
      val diff1=l1.head
      val diff2=l2.head
      List("++")++List(s"'$diff1'")++List("--")++List(s"'$diff2'")++List("\n")++printDiff(l1.tail,l2.tail)
    }
  }

  //Function to compare working directory map and index map
  def compareMaps(m1:Map[String,List[String]],m2:Map[String,List[String]]): Map[String,List[String]]={
    if(m1.isEmpty) m2
    else if (m2.isEmpty) m1
    else
    {val key=m1.head._1
      Map(m1.head._1->compare(m1(s"$key"),m2(s"$key")))++compareMaps(m1.tail,m2.tail)}
  }

  //Function to print the differences
  @scala.annotation.tailrec
  def differencesPrinter(res:Map[String,List[String]]):Unit={
    if(res.isEmpty || res(res.head._1).isEmpty) Unit
    else {
      print(res.head._1+" changes are "+ res(res.head._1).mkString(" ")+"\n")
      differencesPrinter(res.tail)
    }
  }

  def diff(): Unit = {

  val indexFiles=Status.indexContent
    val workingDirFile=Status.workingDirFiles
    val mapIndex=blobsAndContent(indexFiles).filterKeys(_!="")
    val mapDir=dirFilesAndContent(workingDirFile).filterKeys(_!="")

    val differences=compareMaps(mapIndex,mapDir)

    differencesPrinter(differences)







  }

}
