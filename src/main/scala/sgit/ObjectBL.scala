package sgit

import java.io.File

import sgit.Blob.encodeBlob
import sgit.commands.Init
import sgit.utilities.FilesUtilities

object ObjectBL {

  def sha(o:Object) : String = {
    o match {
      case o:Blob  => Blob.shaBlob(o)
      case o:Tree => Tree.shaTree(o)
      case o:Commit=> Commit.shaCommit(o)

    }
  }



  def encodeObject(o:Object) : List[String] = {
      o match {
        case o:Blob => Blob.encodeBlob(o)
        case o:Tree => Tree.encodeTree(o)
        case o:Commit => Commit.encodeCommit(o)
      }
    }


      def addObject(o:Object) :Unit = {
        val sha = ObjectBL.sha(o)
        val directName =Init.RepositoryPath+"/.sgit/objects/"+sha.take(2)
        val fileName =directName+"/"+sha.takeRight(38)
        val file = new File(fileName)
        FilesUtilities.createDirectories(List(directName))
        val b= FilesUtilities.createFiles(List(fileName))
        FilesUtilities.writeInFile(file,encodeObject(o))

      }


}
