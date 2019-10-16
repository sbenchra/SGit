package sgit.commands
import sgit.{Index, IndexEntry}
import java.io.File

import sgit.utilities.FilesUtilities


object Log {
  def logFile(): File=new File(Init.RepositoryPath+"/.sgit/logs")
    //Log content
def logContent:String={
  FilesUtilities.readFileContent(logFile())
}
  //Log content as string splited by \n
  def logContentArray: Array[String] ={logContent.split("\n")}


//Function to return the commits with the parents in a map
  def commitAndParent(logContentA:Array[String]):Map[String,String]={
    if (logContentA.isEmpty) Map()
    else if (logContentA.head.contains("Commit") && !logContentA(logContentA.indexOf(logContentA.head)+3).contains("19011995"))Map(logContentA.head.diff("Commit:")->logContentA(logContentA.indexOf(logContentA.head)+3).diff("Parent:"))++commitAndParent(logContentA.tail)
    else commitAndParent(logContentA.tail)
  }
//Extract tree sha1
  def extractTree(list:List[String]):List[String]={
    if(list.isEmpty) List()
    else if (list.head.contains("tree") && list.head.length>40)
      {list.head.diff("tree").replaceAll(" ","").takeRight(40)::extractTree(list.tail)}
    else extractTree(list.tail)
  }
  //Exctract blobs sha1 and paths
  def extractBlob(list:List[String]):List[String]={
    if(list.isEmpty) List()
    else if (list.head.contains("blob"))
    {list.head.diff("blob")::extractBlob(list.tail)}
    else extractBlob(list.tail)
  }
//Tranform blob from the object directory to map
  def blobsToMap(list:List[String]):Map[String,String]={
    if (list.isEmpty) Map()
    else {
      val blob=list.head.split(" ").drop(1)
      Map(blob.head->blob.last)++blobsToMap(list.tail)
    }
  }
  //Function to list all the trees of a given commit
  def commitTree(trees:List[String]):List[String]={
            if (trees.isEmpty)List()
            else{
              val treesBis=extractTree(FilesUtilities.contentObject(trees.head))
                treesBis++commitTree(trees.tail)++commitTree(treesBis)
            }
          }
//Function to list all the blobs of a given trees
def blobsCommit(trees:List[String]):List[String]={
  if (trees.isEmpty) List()
  else {
    val blobs=extractBlob(FilesUtilities.contentObject(trees.head))
      blobs++blobsCommit(trees.tail)
  }

}
//Function that returns the blobs of a given commit path->sha
  def commitBlobs(commitId:String):Map[String,String]={
        val commitContent= FilesUtilities.contentObject(commitId)
        val treeCommit=extractTree(commitContent)
        blobsToMap(blobsCommit(commitTree(treeCommit)))
  }
//Function to construct all commits blobs
 def constructsCommitMap(commits:Map[String,String]): Map[String,Map[String,String]]={
   if (commits.isEmpty) Map()
   else Map(commits.head._1->commitBlobs(commits.head._1))++Map(commits.head._2->commitBlobs(commits.head._2))++constructsCommitMap(commits.tail)
  }
  //function to transform a map to a list of index entries
  def mapToIndex( map:Map[String,String]):List[IndexEntry]={
    if(map.isEmpty) List()
    else IndexEntry(map.head._1,map.head._2)::mapToIndex(map.tail)
  }
  //Construct the index of each commit
  def constructsIndex(commits:Map[String,Map[String,String]]):Map[String,Index]={
    if (commits.isEmpty) Map()
    else Map(commits.head._1->Index(mapToIndex(commits.head._2)))++constructsIndex(commits.tail)
  }

  def commitIndex(commitsAndParents:Map[String,String]):Map[String,Index]={
    if (commitsAndParents.isEmpty) Map()
    else {
      val commitMap=constructsCommitMap(commitsAndParents)
        constructsIndex(commitMap)
    }
  }
  def blobContents(commitIndex:Map[String,Index]):Map[String,Map[String,List[String]]]={
    if (commitIndex.isEmpty) Map()
    else Map(commitIndex.head._1->Diff.blobsAndContent(commitIndex.head._2))++blobContents(commitIndex.tail)
  }

  @scala.annotation.tailrec
  def checkDiff(blobs1:Map[String,String], blobs2:Map[String,String], diff:Map[String,String]):Unit={
    if(diff.isEmpty) Unit
    else if (blobs1.exists(_._1==diff.head._1) && !blobs2.exists(_._1==diff.head._1))  print("\n"+diff.head._1+" is deleted")
    else if (blobs2.exists(_._1==diff.head._1) && !blobs1.exists(_._1==diff.head._1))  print("\n"+diff.head._1+" is added")
    else if (blobs2.exists(_._2==diff.head._2) && !blobs1.exists(_._2==diff.head._2))  print("\n"+diff.head._1+" is modified")
    else checkDiff(blobs1,blobs2,diff.tail)


  }

  @scala.annotation.tailrec
  def blobsComparaison(commitAndParent:Map[String,String]):Unit={
    if (commitAndParent.isEmpty)Unit
    else {
      val commitTree1= commitTree(List(commitAndParent.head._1))
      val commitTree2= commitTree(List(commitAndParent.head._2))
      val blobs1=blobsCommit(commitTree1)
      val blobs2=blobsCommit(commitTree2)
      val mapBlobs1=blobsToMap(blobs1)
      val mapBlobs2=blobsToMap(blobs2)
      val diff= (mapBlobs1.toSet diff mapBlobs2.toSet).toMap
      checkDiff(mapBlobs1,mapBlobs1,diff)
      blobsComparaison(commitAndParent.tail)
    }



  }

  @scala.annotation.tailrec
  def logBis(commitsAndParents:Map[String,String], blobs:Map[String,Map[String,List[String]]]):Unit={
    if(commitsAndParents.isEmpty)Unit
    else{

      val parent=blobs(commitsAndParents.head._1)
      val commit= blobs(commitsAndParents.head._2)
      print("\nCommit: "+commitsAndParents.head._1+"\n"+"Parent: "+commitsAndParents.head._2+"\n")
      Diff.differencesPrinter(Diff.compareMaps(parent,commit))
      logBis(commitsAndParents.tail,blobs)
    }


  }
  def log():Unit={

    print(logContent)


  }


  def logP():Unit={
    val commitsAndParents=commitAndParent(logContentArray)

    logBis(commitsAndParents,blobContents(commitIndex(commitsAndParents)))
    blobsComparaison(commitsAndParents)


  }


  def main(args: Array[String]): Unit = {
   Init.Init()

   Add.add(List(new File("./soufiane")))


    // Status.status()


    sgit.commands.Commit.commit("s")
    logP()

    //sgit.commands.Diff.diff




    //print(Log.commitAndParent(Log.logContentArray))

    //  print(s)
  }

}
