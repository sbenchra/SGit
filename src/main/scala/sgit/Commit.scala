package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._


import sun.util.calendar.BaseCalendar.Date

case class Commit(
                   authorName: String,
                   authorEmail: String,
                   authorDate: Date,
                   committerName: String,
                   committerEmail: String,
                   commitDate: Date,
                   messageCommit: String,
                   tree : Tree,
                   parentCommit: Commit,
                   cLength:Int

                 ) extends Object {
  override def objectType: ObjectType = ObjectType.commit
  override def length: Int = cLength
}

object Commit{

  def shaCommit(c:Commit):String={
    encodeBodyCommit(c).mkString("").sha1.hex
  }

  //Forming the tree
  def encodeTree(c:Commit): List[String] = {
    List(c.getHeader(c),encodeBodyCommit(c))
  }

  def encodeBodyCommit(c:Commit)= {
    s"tree ${c.sha(c.tree)}\n"+ s"author ${c.authorName} <${c.authorEmail}> ${c.authorDate}\n" + s"committer ${c.committerName} <${c.committerEmail}> ${c.commitDate}\n"+s"ParentCommit ${c.sha(c.parentCommit)}"+ s"\n${c.messageCommit}"


  }



}