import sparknlp.Spark_NLP_Test

object Main {
  def main(args: Array[String]) {
    val env =args(0)
    val spark = SessionBuilder.buildSession(env)

    Spark_NLP_Test.run(spark)

  }
}
