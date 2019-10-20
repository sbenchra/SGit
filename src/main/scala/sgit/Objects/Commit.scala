package sgit.Objects

import java.time.LocalDate

import com.roundeights.hasher.Implicits._
import sgit.Objects.ObjectType.ObjectType

//Commit class

case class Commit(authorName: String,
                  committerName: String,
                  commitDate: LocalDate,
                  messageCommit: String,
                  tree: Tree,
                  parentCommit: String)
    extends Object {
  override def objectType: ObjectType = ObjectType.commit
}

object Commit {
  //Commit length
  def lengthCommit(c: Commit): Int = {
    formBodyCommit(c).length
  }
  //Hash the commit
  def shaCommit(c: Commit): String = {
    formBodyCommit(c).mkString("").sha1.hex
  }

  //forming the body commit
  //@param : c:Commit-> a commit
  //Return: String -> the commit body
  def formBodyCommit(c: Commit): String = {
    s"tree ${ObjectBL.sha(c.tree)}"

  }

  //Forming the tree
  //@param : c:Commit-> a commit
  //Return: List[String] -> the commit content
  def formCommit(c: Commit): List[String] = {
    List(ObjectBL.getHeader(c) + "\n" + formBodyCommit(c))
  }

}
