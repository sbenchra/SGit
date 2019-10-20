package sgit

import java.io.File

import sgit.commands.Init
import sgit.utilities.FilesUtilities

object ObjectBL {

  //The header of a object contains its type and its length
  //@param: o: Object -> an object (Commit,tree or blob)
  //Return : The string of the object
  def getHeader(o: Object): String = {
    s"${o.objectType}" + " " + s"${length(o)}" + "\0"

  }
  // Gives the length of an object
  //@param: o: Object -> an object (Commit,tree or blob)
  //Return : The length of the object

  def length(o: Object): Int = {
    o match {
      case o: Blob   => Blob.lengthBlob(o)
      case o: Tree   => Tree.lengthTree(o)
      case o: Commit => Commit.lengthCommit(o)
    }
  }
  //Get the sha of an object
  //@param: o: Object -> an object (Commit,tree or blob)
  //  Return : String -> object sha
  def sha(o: Object): String = {
    o match {
      case o: Blob   => Blob.shaBlob(o)
      case o: Tree   => Tree.shaTree(o)
      case o: Commit => Commit.shaCommit(o)

    }
  }

  //Function to form an object
  //@param: o: Object -> an object (Commit,tree or blob)
  //  Return : List of string -> object formed
  def formObject(o: Object): List[String] = {
    o match {
      case o: Blob   => Blob.formBlob(o)
      case o: Tree   => Tree.formTree(o)
      case o: Commit => Commit.formCommit(o)
    }
  }

  //Function to add an object to the directory object
  //@param: o: Object -> an object (Commit,tree or blob)
  def addObject(o: Object): Unit = {
    val sha = ObjectBL.sha(o)
    val directName = Init.CurrentDirPath + "/.sgit/objects/" + sha.take(2)
    val fileName = directName + "/" + sha.takeRight(38)
    val file = new File(fileName)
    FilesUtilities.createDirectories(List(directName))
    FilesUtilities.createFiles(List(fileName))
    FilesUtilities.writeInFile(file, formObject(o).map(x => x + "\n"))
  }

  //Extract blobs sha1
  //@param objectContent: List[String] -> list of the object content
  //@return List[String]: -> All blobs in the object
  def extractBlob(objectContent: List[String]): List[String] = {
    if (objectContent.isEmpty) List()
    else if (objectContent.head.contains("blob")) {
      objectContent.head.diff("blob") :: ObjectBL.extractBlob(
        objectContent.tail
      )
    } else ObjectBL.extractBlob(objectContent.tail)
  }

  //Extract tree sha1
  //@param objectContent: List[String] -> list of the object content
  //@return List[String]: -> All trees sha1 in the object
  def extractTree(objectContent: List[String]): List[String] = {
    if (objectContent.isEmpty) List()
    else if (objectContent.head.contains("tree") && objectContent.head.length > 40) {
      objectContent.head
        .diff("tree")
        .replaceAll(" ", "")
        .takeRight(40) :: ObjectBL.extractTree(objectContent.tail)
    } else ObjectBL.extractTree(objectContent.tail)
  }
}
