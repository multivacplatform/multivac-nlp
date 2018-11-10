
import corenlp_simple.{SimplePosTagger, SimplePosTaggerFrench, SimpleTokenizer}
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import com.johnsnowlabs.nlp.annotators.{Normalizer, Stemmer, Tokenizer}
import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.util.Benchmark
import org.apache.spark.ml.feature.NGram

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StopWordsRemover, IDF, HashingTF, CountVectorizer, Word2Vec}

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions._

object Test_NLP_Libraries {

  /**
    * Testing English documents by spark-nlp
    * @param spark is SparkSession type
    * @param inputFile is the path to the JSON Line by Line format
    */
  def Test_English(
                    spark: SparkSession,
                    inputFile: String
                  ): Unit = {

    import spark.implicits._

    val df = spark.read.format("json").option("mode", "DROPMALFORMED").load(inputFile)

    val textColumnName = "title"

    val newsDF = df
      .select(textColumnName)
      .filter("title IS NOT NULL")

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

    val token_finisher = new Finisher()
      .setInputCols("normalized")
      .setOutputCols("tokens_array")
      .setCleanAnnotations(false)
      .setOutputAsArray(true)

    val posOptions = Map("format" -> "text", "repartition" -> "1")
    val posTagger = new PerceptronApproach()
      .setNIterations(5)
      .setInputCols(Array("sentence", "normalized"))
      .setOutputCol("pos")
      .setCorpus(path = "src/main/resources/anc-pos-corpus/110CYL067.txt", delimiter = "|", options = posOptions)
    //      .setCorpus(path = "src/main/resources/masc_tagged/data/spoken", delimiter = "_", readAs = "SPARK_DATASET", options = posOptions)
    //     use pre-trained Pos Tagger
    //    val posTagger = PerceptronModel.pretrained()

    val chunker = new Chunker()
      .setInputCols(Array("pos"))
      .setOutputCol("chunk")
      .setRegexParsers(Array("<DT|PP\\$>?<JJ>*<NN>"))
    //      .setRegexParsers(Array("<DT>?<JJ>*<NN>", "<DT|PP\\$>?<JJ>*<NN>"))

    val token_finisher_pos = new Finisher()
      .setInputCols("pos")
      .setOutputCols("pos_array")
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

    val ngram = new NGram()
      .setN(3)
      .setInputCol("tokens_array")
      .setOutputCol("3-gram")

    val gramAssembler = new DocumentAssembler()
      .setInputCol("3-gram")
      .setOutputCol("3-grams")

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
        token_finisher,
        posTagger,
        chunker,
        token_finisher_pos,
        filteredTokens,
        ngram,
        gramAssembler
        //        cvModel,
        //        idf,
        //        word2Vec
      ))

    val model = Benchmark.time("Time to train model") {
      pipeline.fit(newsDF)
    }

    val pipeLineDF = model.transform(newsDF)

    /*
    Benchmark.time("Time to convert and show") {pipeLineDF.show()}
    println("peipeline DataFrame Schema: ")
    pipeLineDF.printSchema()

    pipeLineDF.select(textColumnName, "3-gram", "3-grams").show(20)
    pipeLineDF.select(textColumnName, "pos.result").show(20)

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
*/
    val phrasesDF = pipeLineDF.withColumn("phrases_array", GrammerExtraction.posPhrases($"pos.result", $"tokens_array"))

    val tokensDF = phrasesDF
      .select(explode($"phrases_array").as("value"))
      .where(size(col("phrases_array")) > 0)
      .groupBy("value")
      .count
      .orderBy($"count".desc)

    tokensDF.printSchema()
    tokensDF.show()

    //    pipeLineDF.select($"chunk.result").where(size(col("chunk.result")) > 0).show()
    val chunkerDF = pipeLineDF
      .select(explode($"chunk.result").as("value"))
      .where(size(col("chunk.result")) > 0)
      .groupBy("value")
      .count
      .orderBy($"count".desc)

    chunkerDF.count
    chunkerDF.show(100, false)

    spark.close()
  }
}