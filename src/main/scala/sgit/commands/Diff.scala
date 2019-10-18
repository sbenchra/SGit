package sgit.commands

import java.io.File

import sgit.{Index, Repository}
import sgit.utilities.FilesUtilities

object Diff {

//Function to put Index blobs contents in a map
  def blobsAndContent(index:Index):Map[String,List[String]]={
    if (index.indexEntries.isEmpty) Map()
    else Map(index.indexEntries.head.path->FilesUtilities.contentObject(index.indexEntries.head.sha).drop(1))++ blobsAndContent(Index(index.indexEntries.tail))
  }
  //Function to put working directory files
  def dirFilesAndContent(files:List[File]):Map[String,List[String]]={
    if (files.isEmpty) Map()
    else Map(files.head.getPath->FilesUtilities.readFileContent(files.head)) ++ dirFilesAndContent(files.tail)
  }

  //Function to compare between to list of lines
  def compare(l1:List[String],l2:List[String]): List[String]= {
    if (l1.isEmpty) l2
    else if (l2.isEmpty) l1
    else {
      val diff1=l2.zipWithIndex.diff(l1.zipWithIndex).map(x=>x._1)
      val diff2=l1.zipWithIndex.diff(l2.zipWithIndex).map(x=>x._1)
      val diffBis1=diff1.diff(l1).filter(x=>x!="")
      val diffBis2=diff2.diff(l2).filter(x=>x!="")

    printDiff( diffBis1,diffBis2)

    }
  }

  def added(listLines:List[String]):List[String]={
    if (listLines.isEmpty) List()
    else List("++"+listLines.head)++added(listLines.tail)
  }

  def deleted(listLines:List[String]):List[String]={
    if (listLines.isEmpty) List()
    else List("--"+listLines.head)++deleted(listLines.tail)
  }


  //Function to prepare the results for the print
  def printDiff(l1:List[String],l2:List[String]):List[String]={
    if (l1.isEmpty && l2.isEmpty) List()
    else {
added(l1)++deleted(l2)


    }

  }

  //Function to compare working directory map and index map
  def compareMaps(m1:Map[String,List[String]],m2:Map[String,List[String]]): Map[String,List[String]]={
    if(m1.isEmpty || m2.isEmpty) Map()
    else
    {
      val mapBlob= Map(m1.head._1->compare(m1(s"${m1.head._1}"),m2(s"${m2.head._1}")))
     mapBlob++compareMaps(m1.filterKeys(_!=s"${m1.head._1}"),m2.filterKeys(_!=s"${m2.head._1}"))}
  }

  //Function to print the differences
  @scala.annotation.tailrec
  def differencesPrinter(res:Map[String,List[String]]):Unit={
    if(res.isEmpty ) Unit
    else if (res.head._2.isEmpty)       differencesPrinter(res.tail)
    else {
      print(res.head._1+" changes are \n"+ res(res.head._1).mkString("\n")+"\n"+res.head._2.count(_.contains("++"))+ "++@@ Ajout"+"\n"+res.head._2.count(_.contains("--"))+ "--@@ Supression")
      differencesPrinter(res.tail)
    }
  }

  @scala.annotation.tailrec
  def statDiff(diff:Map[String,List[String]]):Unit={
    if(diff.isEmpty) Unit
    else {
      println(diff.head._1.split("/").diff(Repository.get.getAbsolutePath.split("/")).mkString("/") +":\n"+diff.head._2.count(_.contains("--"))+"--@@ Suppressions\n"+diff.head._2.count(_.contains("++"))+"++@@ Ajouts")
      statDiff(diff.tail)
    }
  }

  def
  diff(): Unit = {
    val indexFiles=Index.indexContent
    val workingDirFile=Index.workingDirFiles
    val mapIndex=blobsAndContent(indexFiles)
    val mapDir=dirFilesAndContent(workingDirFile)
    val differences=compareMaps(mapIndex,mapDir)
    differencesPrinter(differences)

  }

}
