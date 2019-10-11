package sgit.commands

import java.io.File

import sgit.{Index, Tree, TreeL}

object Commit {

  def transpose[T](l: List[List[T]]): List[List[T]] =
    l.flatMap(_.headOption) match {
      case Nil => Nil
      case head => head :: transpose(l.map(_.drop(1)))
    }


// Recursive function returns the list of paths in the Index
def pathsIndex(index:Index): List[String]= {
  if (index.indexEntries.isEmpty) List()
  else index.indexEntries.head.path::pathsIndex(Index(index.indexEntries.tail))
}
  //Fragmentate the paths to strings
  def fragmentPath(path:List[String]):List[String]={
    if (path.isEmpty) List()
    else path.flatMap(_.split("/").toList).distinct
  }
// Fragment all index paths
  def fargmentAllPaths(paths:List[String]): List[List[String]]={
      if (paths.isEmpty) List()
      else fragmentPath(List(paths.head))::fargmentAllPaths(paths.tail)
  }
  // Form the index tree as a list of level
  def formIndexTree(pathsFragmented:List[List[String]]):List[List[String]]={
    transpose(pathsFragmented)
  }



  def commit():Unit={

    val indexContent= Status.indexContent
    val workingDir=Status.directoryContent




  }

}
