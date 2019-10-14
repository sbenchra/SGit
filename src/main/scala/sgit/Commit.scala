package sgit
import sgit.ObjectType.ObjectType
import com.roundeights.hasher.Implicits._


import sun.util.calendar.BaseCalendar.Date

case class Commit(
                   authorName: String,
                   authorEmail: String,
                   committerName: String,
                   committerEmail: String,
                   commitDate: String,
                   messageCommit: String,
                   tree : Tree,
                   parentCommit: String

                 ) extends Object {
  override def objectType: ObjectType = ObjectType.commit
}

object Commit{

  def lengthCommit(c:Commit):Int= {
    encodeBodyCommit(c).length
  }

  def shaCommit(c:Commit):String={
    encodeBodyCommit(c).mkString("").sha1.hex
  }

  //Forming the tree
  def encodeCommit(c:Commit): List[String] = {
    List(c.getHeader(c)+"\n"+encodeBodyCommit(c))
  }

  def parentCommitToString(pCommits:List[Commit]): String = {
    if (pCommits.isEmpty) ""
    else s"Parent Commit" + pCommits.head + parentCommitToString(pCommits.tail)

  }

  def encodeBodyCommit(c:Commit): String = {
    s"tree ${ObjectBL.sha(c.tree)}\n"+ s"author ${c.authorName} <${c.authorEmail}>" + s"committer ${c.committerName} <${c.committerEmail}> ${c.commitDate}\n"+s"${c.parentCommit}"+ s"\n${c.messageCommit}"


  }



}