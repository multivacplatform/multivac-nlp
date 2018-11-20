package org.multivacplatform

import org.apache.spark.sql.functions.udf

import scala.collection.mutable.ArrayBuffer

object GrammerExtraction  extends Serializable {
  def posPhrases = udf((posArray: Seq[String], tokenArray: Seq[String]) => {
    var resultsOfPostTagger: String = ""
    val posPhrasesArray = ArrayBuffer[String]()
    val tokenArrayWithIndex = tokenArray.zipWithIndex.map{case (k,v) => (v,k)}
    val posArrayWithIndex = posArray.zipWithIndex.map{case (k,v) => (v,k)}
    if(posArray.nonEmpty && tokenArray.nonEmpty){
      for (e <- posArrayWithIndex){
        resultsOfPostTagger += tokenArrayWithIndex.find(_._1 == e._1).get._2 + "_" + e._2 + " "
      }
    }
    val regex = "(?:\\w+_JJ )+\\w+_(?:N[NP])|(?:\\w+_NN )+\\w+_(?:N[NP])".r
    for(m <- regex.findAllIn(resultsOfPostTagger)){
      posPhrasesArray += m.replaceAll("_(?:N[NP]|PRN|JJ)", "")
    }
    // val regex = "(?:\\w+_JJ )+\\w+_(?:N[NP])|(?:\\w+_NN )+\\w+_(?:N[NP])|(?:\\w+_NNP )+\\w+_(?:NNP)".r
    // for(m <- regex.findAllIn(resultsOfPostTagger)){
    //     posPhrasesArray += m.replaceAll("_(?:NN[SP]|NN|PRN|JJ)", "")
    // }
    if(posPhrasesArray.isEmpty){
      posPhrasesArray
    }else{
      posPhrasesArray.map(x => x.toLowerCase).filter(_.length >= 3).filter(_.length <= 20)
    }
  })
}
