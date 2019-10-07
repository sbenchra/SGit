package sgit
import utilities.RepositoryUtilities
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType

case class Blob(size : Int, content :String, bLength:Int) extends Object {
  override def objectType: ObjectType = ObjectType.blob

  override def length: Int = bLength
}
object Blob{

  private def encodeId(b:Blob): String= {
    val header = b.getHeader(b)
    val encodedBlob= header + b.content
     encodedBlob.sha1.hex
  }

 private def encodeBlobBody(b: Blob) : String=
    {
    b.content
    }

  private def encodeBlob(b: Blob) :String ={

   b.objectType +" "+ b.size+" "+ encodeBlob(b)

   }


 }



