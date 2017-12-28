import corenlp_simple.{SimplePosTagger, SimpleTokenizer}

import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import com.johnsnowlabs.nlp.annotators.{Normalizer, RegexTokenizer, Stemmer}
import com.johnsnowlabs.nlp.annotators.pos.perceptron.PerceptronApproach
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetectorModel
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StopWordsRemover, Word2Vec}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.feature.Word2VecModel

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions._

object Test_NLP_Libraries {

  def Test_EnWiki(
                   spark: SparkSession,
                   inputFile: String,
                   lang: String
                 ): Unit ={

    import spark.implicits._

    val df = spark.read.format("json").option("mode", "DROPMALFORMED").load(inputFile)

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

    val sentenceDetector = new SentenceDetectorModel()
      .setInputCols(Array("document"))
      .setOutputCol("sentence")

    val regexTokenizer = new RegexTokenizer()
      .setInputCols(Array("sentence"))
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
    //      .setCorpusPath("src/main/resources/anc-pos-corpus")

    val token_finisher = new Finisher()
      .setInputCols("normalized")
      .setOutputCols("tokens_array")
      .setCleanAnnotations(true)
      .setOutputAsArray(true)

    val stopwords = spark.read.textFile(
      "src/main/resources/stop-words/stopwords_en.txt",
      "src/main/resources/stop-words/stopwords_fr.txt").collect()

    val filteredTokens = new StopWordsRemover()
      .setStopWords(stopwords)
      .setCaseSensitive(false)
      .setInputCol("tokens_array")
      .setOutputCol("filtered")

    //CoreNLP functions
    val corenlp_tokenizer = new SimpleTokenizer()
      .setInputCol(textColumnName)
      .setOutputCol("corenlp_tokens")

    val corenlp_pos = new SimplePosTagger()
      .setInputCol(textColumnName)
      .setOutputCol("corenlp_pos")

    //Spark ML
    val word2Vec = new Word2Vec()
      .setInputCol("filtered")
      .setOutputCol("word2vec")
      .setVectorSize(100)
      .setMinCount(10)
      .setMaxIter(5)

    val pipeline = new Pipeline()
      .setStages(Array(
        documentAssembler,
        sentenceDetector,
        regexTokenizer,
        normalizer,
        stemmer,
        corenlp_tokenizer,
        corenlp_pos,
        posTagger,
        token_finisher,
        filteredTokens,
        word2Vec
      ))

    val model = pipeline.fit(newsDF)

    val pipeLineDF = model.transform(newsDF)

    println("peipeline DataFrame Schema: ")
    pipeLineDF.printSchema()

    pipeLineDF
      .select("filtered")
      //.select("token.result", "corenlp_tokens", "pos.result", "corenlp_pos")
      .show(20, truncate = true)

    val tokensDF = pipeLineDF
      .select(explode($"tokens_array").as("value")) //tokens without stop words
      .groupBy("value")
      .count

    println("number of unique tokens:", tokensDF.count())
    println("total number of tokens:")
    tokensDF.agg(sum("count")).show()
    println("display top 100 tokens:")
    tokensDF.sort($"count".desc).show(20, truncate = false)

    val filtteredTokensDF = pipeLineDF
      .select(explode($"filtered").as("value")) //tokens after stop words
      .groupBy("value")
      .count

    println("number of unique tokens after stop words:", filtteredTokensDF.count())
    println("total number of tokens after stop words:")
    filtteredTokensDF.agg(sum("count")).show()

    println("display top 100 filtered tokens:")
    filtteredTokensDF.sort($"count".desc).show(20, truncate = false)

    //    pipeLineDF.select("word2vec").show(50, truncate = false)

    val word2VecModel = model.stages.last.asInstanceOf[Word2VecModel]
    if(lang == "en"){
      word2VecModel.findSynonyms("london", 4).show(false)
      word2VecModel.findSynonyms("france", 4).show(false)
      word2VecModel.findSynonyms("monday", 4).show(false)
    } else if(lang == "fr"){
      word2VecModel.findSynonyms("france", 4).show(false)
      word2VecModel.findSynonyms("politique", 4).show(false)
      word2VecModel.findSynonyms("lundi", 4).show(false)
    }

    spark.close()
  }
}