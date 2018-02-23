package sparknlp

import com.johnsnowlabs.nlp._
import com.johnsnowlabs.nlp.annotators._
import com.johnsnowlabs.nlp.annotators.pos.perceptron.PerceptronApproach
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.sql.SparkSession


object Spark_NLP_Test {
  def test(spark: SparkSession): Unit ={

    println("Spark Version", spark.version)

    val inputFile="src/main/resources/enwikinews.json"

    val df = spark.read.format("json").option("mode", "DROPMALFORMED").load(inputFile)

    val newsDF = df.select("id", "title").filter("id IS NOT NULL AND title IS NOT NULL")
    newsDF.show(10)

    val documentAssembler = new DocumentAssembler()
      .setInputCol("title")
      .setOutputCol("document")

    val sentenceDetector = new SentenceDetector()
      .setInputCols(Array("document"))
      .setOutputCol("sentence")

    val regexTokenizer = new Tokenizer()
      .setInputCols(Array("sentence"))
      .setOutputCol("token")

    val stopwords = spark.read.textFile("src/main/resources/stopwords_en.txt").collect()
    val filteredTokens = new StopWordsRemover()
      .setStopWords(stopwords)
      .setCaseSensitive(false)
      .setInputCol("token")
      .setOutputCol("filtered")

    val normalizer = new Normalizer()
      .setInputCols(Array("token"))
      .setOutputCol("normalized")

    val stemmer = new Stemmer()
      .setInputCols(Array("token"))
      .setOutputCol("stem")

    val posTagger = new PerceptronApproach()
      .setNIterations(20)
      .setInputCols(Array("sentence", "token"))
      .setOutputCol("pos")

    val finisher = new Finisher()
      .setInputCols("token")
      .setCleanAnnotations(false)

    val pipeline = new Pipeline()
      .setStages(Array(
        documentAssembler,
        sentenceDetector,
        regexTokenizer,
        normalizer,
        stemmer,
        posTagger,
        finisher
      ))

    val pipeLineDF = pipeline
      .fit(newsDF)
      .transform(newsDF)

    pipeLineDF.printSchema()
    pipeLineDF.select("token.result", "pos.result")show(20, false)

    spark.close()
  }
}
