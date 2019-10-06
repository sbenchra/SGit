package sgit
import sgit.Repository
import utilities.RepositoryUtilities
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType

case class Blob(size : Int, content :Option[Seq[Byte]] = None) extends Object {
  override def objectType: ObjectType = ObjectType.blob

  override def length: Int = 0
}
object Blob{

   def encodeBlobId(b: Blob): String ={
     val header = b.getHeader(b)
     val encodedBlob= header + b.content.getOrElse(Seq(""))
     // Generate a few hashes
      encodedBlob.sha1.hex
   }
   def createBlob(b: Blob) :Unit ={
     val dirPath = System.getProperty("user.dir")
     val directoryName=encodeBlobId(b).take(3)
     val directoryPath = s"$dirPath/.sgit/objects"+"/"+s"$directoryName"
     val blobName= encodeBlobId(b).takeRight(38)
     RepositoryUtilities.createDirectories(List(directoryPath))
     RepositoryUtilities.createFiles(List(directoryPath+"/"+blobName))


   }



 }



