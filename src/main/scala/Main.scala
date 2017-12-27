import corenlp_simple.SimpleAPI_Test
import sparknlp.Spark_NLP_Test

object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)
    val inputFile="src/main/resources/enwikinews.json"

    //    CoreNLP_SimpleAPI_Test.test()
    //    Spark_NLP_Test.test(spark)

    Test_NLP_Libraries.Test_EnWiki(spark, inputFile)
  }
}