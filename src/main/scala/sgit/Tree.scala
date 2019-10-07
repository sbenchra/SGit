package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._



case class Tree(content : Seq[TreeL]) extends Object {
  override def objectType: ObjectType = ObjectType.tree
  override def length: Int = content.length
}


object Tree{

  def shaTree(t:Tree): String= {
      encodeTree(t).mkString("").sha1.hex

  }
  //Forming the tree
  def encodeTree(t:Tree): List[String] = {
    List(t.getHeader(t)+" "+encodeBodyTree(t))
  }

  //Tree body
  def encodeBodyTree(t:Tree): String = {
   if (t.content.isEmpty) ""
   else
     " \n" + t.content.head.objectType +" "+ t.content.head.name+" " + t.content.head.lSha +" " +encodeBodyTree(Tree(t.content.tail))

  }
}