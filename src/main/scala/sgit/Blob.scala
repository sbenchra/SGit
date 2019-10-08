
package sgit
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType

case class Blob( content :String) extends Object {
  override def objectType: ObjectType = ObjectType.blob

}
object Blob{

  def lengthBlob(b:Blob): Int = b.content.length


  def shaBlob(b:Blob): String= {

     encodeBlob(b).mkString("").sha1.hex
  }

 private def encodeBlobBody(b: Blob) : String=
    {
    b.content
    }

   def encodeBlob(b: Blob) :List[String] ={

   List(b.getHeader(b),encodeBlobBody(b))

   }


 }



