
package sgit
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType

case class Blob( content :String, bLength:Int) extends Object {
  override def objectType: ObjectType = ObjectType.blob
  override def length: Int = bLength

}
object Blob{

  def shaBlob(b:Blob): String= {

     encodeBlob(b).mkString("").sha1.hex
  }

 private def encodeBlobBody(b: Blob) : String=
    {
    b.content
    }

   def encodeBlob(b: Blob) :List[String] ={

   List(b.getHeader(b)+" "+ encodeBlobBody(b))

   }


 }



