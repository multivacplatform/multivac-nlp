import corenlp_simple.SimpleAPI_Test
import sparknlp.Spark_NLP_Test

object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)
    // test with Wiki News
    val wikiNewsJSON="src/main/resources/data/enwikinews.json"

    // test with Tweet sample
    // val tweetSampleJSON="src/main/resources/data/tweets_sample.json"

    SimpleAPI_Test.testEnglish()
    SimpleAPI_Test.testFrench()

//    Test_NLP_Libraries.Test_EnWiki(
//      spark,
//      wikiNewsJSON,
//      "en"
//    )
  }
}