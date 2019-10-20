package sgit

//Enumeration of Objects type
object ObjectType extends Enumeration {
  type ObjectType = Value
  val commit = Value("commit")
  val tree = Value("tree")
  val blob = Value("blob")
  val tag = Value("tag")
}
