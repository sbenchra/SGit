package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._



abstract class Object{
  def objectType : ObjectType
  def length: Int
  def getHeader(o:Object ): String = {
    s"{${o.objectType}}"+" "+s"{${o.length}}"

  }
}




