package sgit.Objects

//Tag Class
case class Tag(name: String) extends Object {
  override def objectType: ObjectType = ObjectType.tag
}
