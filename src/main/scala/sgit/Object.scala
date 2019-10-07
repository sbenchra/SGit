package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._

// Abstract class of Object
// Type of the object
// length of the object

abstract class Object{
  def objectType : ObjectType
  def length: Int
  // Returns the header of the object
  def getHeader(o:Object ): String = {
    s"{${o.objectType}}"+" "+s"{${o.length}}"

  }
}




