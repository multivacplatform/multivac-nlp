package corenlp_simple

import scala.collection.JavaConverters._
import edu.stanford.nlp.simple._

object SimpleAPI_Functions extends Serializable {
  def getWords(document: String): Seq[String] = {
    new Sentence(document).words().asScala
  }
}