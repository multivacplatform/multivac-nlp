import corenlp_simple.{SimplePosTagger, SimplePosTaggerFrench, SimpleTokenizer}
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import com.johnsnowlabs.nlp.annotators.{Normalizer, Tokenizer, Stemmer}
import com.johnsnowlabs.nlp.annotators.pos.perceptron.PerceptronApproach
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.{SentenceDetector}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions._

object Test_NLP_Libraries {

  def Test_English(
                    spark: SparkSession,
                    inputFile: String
                  ): Unit ={

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

    //spark-nlp functions
    val documentAssembler = new DocumentAssembler()
      .setInputCol(textColumnName)
      .setOutputCol("document")

    val sentenceDetector = new SentenceDetector()
      .setInputCols(Array("document"))
      .setOutputCol("sentence")

    val token = new Tokenizer()
      .setInputCols(Array("document"))
      .setOutputCol("token")

    val normalizer = new Normalizer()
      .setInputCols(Array("token"))
      .setOutputCol("normalized")

    val stemmer = new Stemmer()
      .setInputCols(Array("normalized"))
      .setOutputCol("stem")

    val posTagger = new PerceptronApproach()
      .setNIterations(5)
      .setInputCols(Array("sentence", "token"))
      .setOutputCol("pos")

    val token_finisher = new Finisher()
      .setInputCols("normalized")
      .setOutputCols("tokens_array")
      .setCleanAnnotations(false)
      .setOutputAsArray(true)

    //CoreNLP functions
    val corenlp_tokenizer = new SimpleTokenizer()
      .setInputCol(textColumnName)
      .setOutputCol("corenlp_tokens")

    val corenlp_pos = new SimplePosTagger()
      .setInputCol(textColumnName)
      .setOutputCol("pos")

    //Spark ML
    val stopwords = spark.read.textFile(
      "src/main/resources/stop-words/stopwords_en.txt",
      "src/main/resources/stop-words/stopwords_fr.txt").collect()

    val filteredTokens = new StopWordsRemover()
      .setStopWords(stopwords)
      .setCaseSensitive(false)
      .setInputCol("tokens_array")
      .setOutputCol("filtered")

    val cvModel = new CountVectorizer()
      .setInputCol("filtered")
      .setOutputCol("rawFeatures")
      .setVocabSize(1000)
      .setMinDF(2)
      .setMinTF(5)

    val hashingTF = new HashingTF()
      .setInputCol("filtered")
      .setOutputCol("rawFeatures")
      .setNumFeatures(20)

    val idf = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")

    val word2Vec = new Word2Vec()
      .setInputCol("filtered")
      .setOutputCol("word2vec")
      .setVectorSize(100)
      .setMinCount(10)
      .setMaxIter(20)

    val pipeline = new Pipeline()
      .setStages(Array(
        documentAssembler,
        sentenceDetector,
        token,
        normalizer,
        stemmer,
        posTagger,
        token_finisher,
        filteredTokens
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
    pipeLineDF.show()
    pipeLineDF.select("pos.result").show(20)

    val tokensDF = pipeLineDF
      .select(explode($"tokens_array").as("value")) //tokens without stop words
      .groupBy("value")
      .count
      .orderBy($"count".desc)

    println("number of unique tokens:", pipeLineDF.count())
    println("number of unique tokens:", tokensDF.count())
    println("display top 20 tokens:")
    tokensDF.show(20)

    val filtteredTokensDF = pipeLineDF
      .select(explode($"filtered").as("value")) //tokens after stop words
      .groupBy("value")
      .count

    println("number of unique tokens after stop words:", filtteredTokensDF.count())
    println("total number of tokens after stop words:")
    filtteredTokensDF.agg(sum("count")).show()

    println("display top 20 filtered tokens:")
    filtteredTokensDF.sort($"count".desc).show(20, truncate = false)

    // Vocabs in CountVectorizerModel
    //    val cvModelPipeline = model.stages(9).asInstanceOf[CountVectorizerModel]
    //    val vocabArray = cvModelPipeline.vocabulary
    //    vocabArray.foreach(println)

    // word2VecModel
    //    val word2VecModel = model.stages.last.asInstanceOf[Word2VecModel]
    //    word2VecModel.findSynonyms("london", 4).show(false)
    //    word2VecModel.findSynonyms("france", 4).show(false)
    //    word2VecModel.findSynonyms("monday", 4).show(false)

    spark.close()
  }
}