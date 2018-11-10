package sparkml

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.functions.explode
import org.apache.spark.sql.functions._

object Spark_ML_NLP {

  /**
    * Test Spark ML for text mining and NLP for English documents
    * @param spark is a shared SparkSession
    * @param inputFile is the path to the JSON Line by Line format
    */
  def Test_English(
                    spark: SparkSession,
                    inputFile: String
                  ): Unit = {

    var startTime = System.nanoTime()
    println(s"==========")
    println(s"Start loading data")

    import spark.implicits._

    val df = spark.read.format("json").option("mode", "DROPMALFORMED").load(inputFile)

    var elapsed = (System.nanoTime() - startTime) / 1e9
    println(s"Finished loading data.")
    println(s"Time (sec)\t$elapsed")
    println(s"==========")

    val textColumnName = "text"

    val newsDF = df
      .select(textColumnName)
      .filter("text IS NOT NULL")

    newsDF.cache()

    println("WikiNews Number of articles: ", newsDF.count())

    //Spark ML
    val stopwords = spark.read.textFile(
      "src/main/resources/stop-words/stopwords_en.txt",
      "src/main/resources/stop-words/stopwords_fr.txt").collect()

    val regexTokenizer = new RegexTokenizer()
      .setInputCol(textColumnName)
      .setOutputCol("words")
      .setToLowercase(true)
      .setPattern("\\W") // alternatively .setPattern("\\w+").setGaps(false)

    val filteredTokens = new StopWordsRemover()
      .setStopWords(stopwords)
      .setCaseSensitive(false)
      .setInputCol("words")
      .setOutputCol("filtered")

    val bigram = new NGram()
      .setN(2)
      .setInputCol("filtered")
      .setOutputCol("bigram")

    val trigram = new NGram()
      .setN(3)
      .setInputCol("filtered")
      .setOutputCol("trigram")

    val pipeline = new Pipeline()
      .setStages(Array(
        regexTokenizer,
        filteredTokens,
        bigram,
        trigram
      ))

    startTime = System.nanoTime()
    println(s"==========")
    println(s"Fit the Pipeline")

    val model = pipeline.fit(newsDF)

    println(s"==========")
    println(s"Transform the Pipeline")

    val pipeLineDF = model.transform(newsDF)

    elapsed = (System.nanoTime() - startTime) / 1e9
    println(s"Finished training and transforming Pipeline")
    println(s"Time (sec)\t$elapsed")
    println(s"==========")

    println("peipeline DataFrame Schema: ")
    pipeLineDF.printSchema()
    pipeLineDF.show(20)


    val mergeColumnsArray = udf((a: Seq[String], b: Seq[String]) => a++b)
    val mergedNGramDF = pipeLineDF.withColumn("ngrams",mergeColumnsArray(pipeLineDF("bigram"),pipeLineDF("trigram"))).select(textColumnName, "ngrams")

    val hashingTF = new HashingTF()
      .setInputCol("ngrams")
      .setOutputCol("hashingTF")
      .setNumFeatures(5000)

    val idf = new IDF()
      .setInputCol("hashingTF")
      .setOutputCol("idf")

    val normalizer = new Normalizer()
      .setInputCol("idf")
      .setOutputCol("features")

    // Trains a k-means model.
    val kmeans = new KMeans()
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
      .setK(50)
      .setMaxIter(100)
      .setSeed(0) // fpr reproducability

    val pipeline2 = new Pipeline()
      .setStages(Array(
        hashingTF,
        idf,
        normalizer,
        kmeans
        //        cvModel,
        //        idf,
        //        word2Vec
      ))

    startTime = System.nanoTime()
    println(s"==========")
    println(s"Fit the Pipeline")

    val model2 = pipeline2.fit(mergedNGramDF)

    println(s"==========")
    println(s"Transform the Pipeline")

    val pipeLineDF2 = model2.transform(mergedNGramDF)

    elapsed = (System.nanoTime() - startTime) / 1e9
    println(s"Finished training and transforming Pipeline")
    println(s"Time (sec)\t$elapsed")
    println(s"==========")

    println("peipeline DataFrame Schema: ")
    pipeLineDF2.printSchema()
    pipeLineDF2.show(20)

    pipeLineDF2.groupBy($"prediction")
      .count
      .orderBy($"count".desc)
      .show(100)

    val categories = pipeLineDF2
      .select(textColumnName, "ngrams", "prediction")
      .filter($"prediction" === 13)

    categories.select(
      explode($"ngrams").as("value")
    )
      .groupBy("value")
      .count
      .orderBy($"count".desc)
      .show(100, false)

    //    val result = pipeLineDF.withColumn("combined", array($"bigram", $"trigram"))
    //    result.select("combined").show(false)


    //    val tokensDF = pipeLineDF
    //      .select(
    //        explode($"tokens").as("value")
    //      ) //tokens without stop words
    //      .groupBy("value")
    //      .count
    //      .orderBy($"count".desc)

    //    tokensDF.count()
    //    tokensDF.show(50)

  }
}
