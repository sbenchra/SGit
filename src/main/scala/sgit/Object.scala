package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._

// Abstract class of Object
// Type of the object
// length of the object

abstract class Object{

  def objectType : ObjectType

  // Returns the header of the object
  def getHeader(o:Object ): String = {
    s"${o.objectType}"+" "+s"${length(o)}"+"\0"

  }
  // Gives the length of an object
  def length(o: Object) : Int= {
    o match {
      case o:Blob => Blob.lengthBlob(o)
      case o:Tree => Tree.lengthTree(o)
      case o:Commit => Commit.lengthCommit(o)
    }
  }



}




