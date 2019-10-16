package sgit.commands

import java.io.File
import sgit.Index
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
    else Map(files.head.getPath->FilesUtilities.readFileContent(files.head).split("\n").toList) ++ dirFilesAndContent(files.tail)
  }

  //Function to compare between to list of lines
  def compare(l1:List[String],l2:List[String]): List[String]= {
    if (l1.isEmpty) l2
    else if (l2.isEmpty) l1
    else {
      val diff1=l2.zipWithIndex.diff(l1.zipWithIndex).map(x=>x._1)
      val diff2=l1.zipWithIndex.diff(l2.zipWithIndex).map(x=>x._1)
      val diffBis1=diff1.diff(l1)
      val diffBis2=diff2.diff(l2)

    printDiff( diffBis1,diffBis2)
    }
  }


  //Function to prepare the results for the print
  def printDiff(l1:List[String],l2:List[String]):List[String]={
    if (l1.isEmpty && l2.isEmpty) List()
    else if(l2.isEmpty) List("++"+s"${l1.mkString("")}")++List("--  ")
    else if (l1.isEmpty) List("++ ")++List("-- "+s"${l2.mkString("")}")
    else{

      List("++"+s"${l2.head}")++List("--"+s"${l1.head}")++printDiff(l1.tail,l2.tail)

    }
  }

  //Function to compare working directory map and index map
  def compareMaps(m1:Map[String,List[String]],m2:Map[String,List[String]]): Map[String,List[String]]={
    if(m1.isEmpty) m2
    else if (m2.isEmpty) m1
    else
    {
      Map(m1.head._1->compare(m1(s"${m1.head._1}"),m2(s"${m2.head._1}")))++compareMaps(m1.filterKeys(_!=s"${m1.head._1}"),m2.filterKeys(_!=s"${m2.head._1}"))}
  }

  //Function to print the differences
  @scala.annotation.tailrec
  def differencesPrinter(res:Map[String,List[String]]):Unit={
    if(res.isEmpty ) Unit
    else if (res.head._2.isEmpty)       differencesPrinter(res.tail)
    else {
      print(res.head._1+" changes are \n"+ res(res.head._1).mkString("\n")+"\n"+res.head._2.length/2+ "++--@@ Modifications")
      differencesPrinter(res.tail)
    }
  }

  @scala.annotation.tailrec
  def lengthDiff(diff:Map[String,List[String]]):Unit={
    if(diff.isEmpty) Unit
    else {
      println(diff.head._2.length/2+ "++--@@ Modifications")
      lengthDiff(diff.tail)
    }
  }

  def diff(): Unit = {

    val indexFiles=Index.indexContent
    val workingDirFile=Index.workingDirFiles
    val mapIndex=blobsAndContent(indexFiles)
    val mapDir=dirFilesAndContent(workingDirFile)
    val differences=compareMaps(mapIndex,mapDir)
    differencesPrinter(differences)


  }


  def main(args: Array[String]): Unit = {
    Init.Init()
    // Add.add(List(new File("./soufiane")))
    // Status.status()
    //sgit.commands.Commit.commit("d")
    //logP()
    //Diff.diff()
    //Branch.branchAv()
    //FilesUtilities.deleteContentIndex(Array(" "))

    //println(FilesUtilities.indexContentBis.map(_.head))
    //  print(s)
  }

}
