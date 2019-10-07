package sgit

import sgit.Blob.encodeBlob
import sgit.utilities.FilesUtilities

object ObjectBL {

    def encodeObject(o:Object) : List[String] = {
      o match {
        case o:Blob => Blob.encodeBlob(o)
        case o:Tree => Tree.encodeTree(o)
      }
    }
      def addObject(o:Object) :Unit = {
        val sha = o.sha(o)
        val directName =Repository.dirPath+"/.sgit/objects/"+sha.take(2)
        val fileName =directName+"/"+sha.takeRight(38)
        FilesUtilities.createDirectories(List(directName))
        FilesUtilities.createFiles(List(fileName))
        val test= encodeObject(o)
        FilesUtilities.writeInFiles(List(fileName),test)
      }

}
