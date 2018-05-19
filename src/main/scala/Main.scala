import corenlp_simple.SimpleAPI_Test
import sparkml.Spark_ML_NLP
import sparknlp.Spark_NLP_Test

object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)
    // test with Wiki News
    val wikiNewsJSON="data/enwikinews.json"

    // test with Tweet sample
    //     val tweetSampleJSON="data/tweets_sample.json"

    //    SimpleAPI_Test.testEnglish()
    //    SimpleAPI_Test.testFrench()

    Test_NLP_Libraries.Test_English(
      spark,
      wikiNewsJSON
    )
    //    Spark_ML_NLP.Test_English(
    //      spark,
    //      wikiNewsJSON
    //    )

  }
}