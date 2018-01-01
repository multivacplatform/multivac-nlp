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

  def getPOS(document: String, lang: String): Seq[String] = {
    val props = new Properties()
    props.setProperty("tokenize.language", lang)

    new Sentence(document, props)
      .posTags(props)
      .asScala
  }
}