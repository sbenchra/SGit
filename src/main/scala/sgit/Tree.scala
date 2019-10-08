package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._



case class Tree(contentTree : Seq[TreeL]) extends Object {
  override def objectType: ObjectType = ObjectType.tree
}


object Tree{

  def lengthTree(t: Tree): Int = { encodeBodyTree(t).length }

  def shaTree(t:Tree): String= {
      encodeTree(t).mkString("").sha1.hex

  }
  //Forming the tree
  def encodeTree(t:Tree): List[String] = {
    List(t.getHeader(t),encodeBodyTree(t))
  }

  //Tree body
  def encodeBodyTree(t:Tree): String = {
   if (t.contentTree.isEmpty) ""
   else
     " \n" + t.contentTree.head.objectType +" "+ t.contentTree.head.name+" " + t.contentTree.head.lSha +" " +encodeBodyTree(Tree(t.contentTree.tail))

  }
}