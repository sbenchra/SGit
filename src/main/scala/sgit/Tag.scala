package sgit
import sgit.ObjectType.ObjectType


case class Tag(name:String) extends Object {
  override def objectType: ObjectType = ObjectType.tag
}
