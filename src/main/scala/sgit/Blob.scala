
package sgit
import com.roundeights.hasher.Implicits._
import sgit.ObjectType.ObjectType
// Blob
case class Blob( content :List[String]) extends Object {
  override def objectType: ObjectType = ObjectType.blob

}
object Blob{
//Length of blob
  def lengthBlob(b:Blob): Int = b.content.length

//Generating the sha of the blob
  def shaBlob(b:Blob): String= {

     formBlob(b).mkString("").sha1.hex
  }
//Function to form a blob body
 private def formBlobBody(b: Blob) : List[String]=
    {
    b.content
    }
//Function to form a Blob to write it in a file
   def formBlob(b: Blob) :List[String] ={

   List(ObjectBL.getHeader(b)+"\n")++formBlobBody(b)

   }


 }



