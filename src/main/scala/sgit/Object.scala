package sgit

import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._

// Abstract class of Object

abstract class Object {
  def objectType: ObjectType

  // Returns the header of the object

}
