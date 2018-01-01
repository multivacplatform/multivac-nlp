package corenlp_simple

import scala.collection.JavaConverters._
import java.util.Properties
import edu.stanford.nlp.simple._

object SimpleAPI_Functions extends Serializable {

  def getWords(document: String): Seq[String] = {
    new Sentence(document)
      .words()
      .asScala
  }

  def getPOS(document: String): Seq[String] = {
    new Sentence(document)
      .posTags()
      .asScala
  }

  def getPOSFrench(document: String): Seq[String] = {
    val props = new Properties()
    props.setProperty("tokenize.language", "fr")
    props.setProperty("language", "french")
    props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/french/french.tagger")

    new Sentence(document, props)
      .posTags(props)
      .asScala
  }
}