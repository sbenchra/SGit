
package sgit
import utilities.FilesUtilities
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType

case class Blob( content :String, bLength:Int) extends Object {
  override def objectType: ObjectType = ObjectType.blob
  override def length: Int = bLength

}
object Blob{

  def shaBlob(b:Blob): String= {
    val header = b.getHeader(b)
    val encodedBlob= header + b.content
     encodedBlob.sha1.hex
  }

 private def encodeBlobBody(b: Blob) : String=
    {
    b.content
    }

   def encodeBlob(b: Blob) :List[String] ={

   List(b.objectType +" "+ b.length +" "+ encodeBlobBody(b))

   }


 }



