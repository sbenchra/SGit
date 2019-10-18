package sgit

import java.io.File

import sgit.Blob.formBlob
import sgit.commands.Init
import sgit.utilities.FilesUtilities

object ObjectBL {


  def getHeader(o:Object ): String = {
    s"${o.objectType}"+" "+s"${length(o)}"+"\0"

  }
  // Gives the length of an object
  def length(o: Object) : Int= {
    o match {
      case o: Blob => Blob.lengthBlob(o)
      case o: Tree => Tree.lengthTree(o)
      case o: Commit => Commit.lengthCommit(o)
    }
  }
//Get the sha of an object
  def sha(o:Object) : String = {
    o match {
      case o:Blob  => Blob.shaBlob(o)
      case o:Tree => Tree.shaTree(o)
      case o:Commit=> Commit.shaCommit(o)

    }
  }

  //Function to form an object
  def formObject(o:Object) : List[String] = {
      o match {
        case o:Blob => Blob.formBlob(o)
        case o:Tree => Tree.formTree(o)
        case o:Commit => Commit.encodeCommit(o)
      }
    }

//Function to add an object to the directory object
      def addObject(o:Object) :Unit = {
        val sha = ObjectBL.sha(o)
        val directName =Init.CurrentDirPath+"/.sgit/objects/"+sha.take(2)
        val fileName =directName+"/"+sha.takeRight(38)
        val file = new File(fileName)
        FilesUtilities.createDirectories(List(directName))
        FilesUtilities.createFiles(List(fileName))
        FilesUtilities.writeInFile(file,formObject(o).map(x=>x+"\n"))
      }


}
