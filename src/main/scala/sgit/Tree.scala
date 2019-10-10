package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._

// Class of Tree entries

case class TreeL(objectType:String,name:String,lSha: String)
case class Tree(contentTree : Seq[TreeL]) extends Object {
  override def objectType: ObjectType = ObjectType.tree
}


object Tree{

  def lengthTree(t: Tree): Int = { formBodyTree(t).length }

  def shaTree(t:Tree): String= {
      formTree(t).mkString("").sha1.hex

  }
  //Forming the tree
  def formTree(t:Tree): List[String] = {
    List(t.getHeader(t),formBodyTree(t))
  }

  //Tree body
  def formBodyTree(t:Tree): String = {
   if (t.contentTree.isEmpty) ""
   else
     " \n" + t.contentTree.head.objectType +" "+ t.contentTree.head.name+" " + t.contentTree.head.lSha +" " +formBodyTree(Tree(t.contentTree.tail))

  }
}