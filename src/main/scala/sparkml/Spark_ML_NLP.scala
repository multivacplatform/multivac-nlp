package sparkml

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.functions.explode
import org.apache.spark.sql.functions._

object Spark_ML_NLP {

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

    val ngramsCols = Array("filtered", "bigram", "trigram")

    //    val assembler = new VectorAssembler()
    //      .setInputCols(ngramsCols)
    //      .setOutputCol("tokens")

    val hashingTF = new HashingTF()
      .setInputCol("filtered")
      .setOutputCol("hashingTF")
      .setNumFeatures(20000)

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
      .setK(100)
      .setMaxIter(50)
      .setSeed(0) // fpr reproducability

    val pipeline = new Pipeline()
      .setStages(Array(
        regexTokenizer,
        filteredTokens,
        bigram,
        trigram,
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

    pipeLineDF.groupBy($"prediction").count.show(100)

    val categories = pipeLineDF
      .select(textColumnName, "prediction")
      .filter($"prediction" === 11)

    categories.show(20, false)
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
