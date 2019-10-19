package sgit
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType

// Class of Tree entries

case class TreeL(objectType: ObjectType, path: String, lSha: String)
case class Tree(contentTree: List[TreeL]) extends Object {
  override def objectType: ObjectType = ObjectType.tree
}

object Tree {
  //Length of the tree body
  def lengthTree(t: Tree): Int = { formBodyTree(t).length }
//Hashing the tree
  def shaTree(t: Tree): String = {
    formTree(t).mkString("").sha1.hex

  }
  //Forming the tree
  def formTree(t: Tree): List[String] = {
    List(ObjectBL.getHeader(t) + "\n" + formBodyTree(t))
  }

  //Tree body
  def formBodyTree(t: Tree): String = {
    if (t.contentTree.isEmpty) ""
    else
      " \n" + t.contentTree.head.objectType + " " + t.contentTree.head.path + " " + t.contentTree.head.lSha + " " + formBodyTree(
        Tree(t.contentTree.tail)
      )

  }
}
