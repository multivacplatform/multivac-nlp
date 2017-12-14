package corenlp

import edu.stanford.nlp.simple._

import org.apache.spark.sql.functions.udf

import scala.collection.JavaConverters._
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object CoreNLP_UDF extends Serializable {
  def textCleaning(s:String): String={
    var st=s
    st =st.replaceAll("[\\`\\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+\\=\\-\\{\\}\\[\\]\\;\\:\\'\\\"\\<\\>\\,\\.\\/\\?]", "")
    //println(s+"\t"+st)
    st
  }
}
