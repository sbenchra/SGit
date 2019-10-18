package sgit
import sgit.ObjectType.ObjectType
import java.time.LocalDate
import com.roundeights.hasher.Implicits._


//Commit class

case class Commit(
                   authorName: String,
                   committerName: String,
                   commitDate:LocalDate,
                   messageCommit: String,
                   tree : Tree,
                   parentCommit: String

                 ) extends Object {
  override def objectType: ObjectType = ObjectType.commit
}

object Commit{
//Commit length
  def lengthCommit(c:Commit):Int= {
    formBodyCommit(c).length
  }
//Hash the commit
  def shaCommit(c:Commit):String={
    formBodyCommit(c).mkString("").sha1.hex
  }

  //Forming the tree
  //@param : c:Commit-> a commit
  //Return: List[String] -> the commit content
  def formCommit(c:Commit): List[String] = {
    List(ObjectBL.getHeader(c)+"\n"+formBodyCommit(c))
  }
  //forming the body commit
  //@param : c:Commit-> a commit
  //Return: String -> the commit body
  def formBodyCommit(c:Commit): String = {
    s"tree ${ObjectBL.sha(c.tree)}\n"+ s"author ${c.authorName}" + s"committer ${c.committerName}  ${c.commitDate}\n"+ s"\n${c.messageCommit}"+ s"\n${c.parentCommit}"


  }



}