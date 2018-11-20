package org.multivacplatform

import org.multivacplatform.corenlp_simple.SimpleAPI_Test
import org.multivacplatform.nlp.Multivac
import org.multivacplatform.sparkml.Spark_ML_NLP

object Main {
  def main(args: Array[String]) {

    val multivacUtil = new Multivac

//    multivacUtil.apply("test")
    val spark = SessionBuilder.buildSession()
    println(spark.version)

    import spark.implicits._

    val rawData = List("""What if Google Morphed Into GoogleOS? What if Google expanded on its search-engine (and now e-mail) wares into a full-fledged operating system? [via Microsoft Watch from Mary Jo Foley ]""")

    val testEnglishDF = rawData.toDF("content")


    val multivacModel = multivacUtil.init("en")

    val manualPipelineDF = multivacUtil.apply(
      multivacModel,
      testEnglishDF
    )
    manualPipelineDF.select("token.result", "pos.result").show(false)
    /*
        // test with Wiki News
        val wikiNewsJSON="src/main/resources/data/enwikinews.json"

        SimpleAPI_Test.testEnglish()
        SimpleAPI_Test.testFrench()

        Test_NLP_Libraries.Test_English(spark, wikiNewsJSON)
        Spark_ML_NLP.Test_English(spark, wikiNewsJSON)
    */
    spark.close()
  }
}
