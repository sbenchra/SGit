package sgit

import java.io.File

import sgit.utilities.FilesUtilities

case class IndexEntry(path:String,sha:String)

case class Index(indexEntries: List[IndexEntry],repositoryPath: String)

object Index{

  def encodeIndex(index: Index) : Unit={
    val repPath= index.repositoryPath
    if(index.indexEntries.isEmpty) print("")
    FilesUtilities.writeInFile(new File(repPath),List(index.indexEntries.head.path + index.indexEntries.head.sha))
    encodeIndex(new Index(index.indexEntries.tail,repPath))
  }

}