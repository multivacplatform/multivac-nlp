import corenlp.CoreNLP_SimpleAPI_Test
import sparknlp.Spark_NLP_Test

object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)


    CoreNLP_SimpleAPI_Test.test()
    Spark_NLP_Test.test(spark)
  }
}
