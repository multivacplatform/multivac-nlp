import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import com.johnsnowlabs.nlp.annotators.{Normalizer, RegexTokenizer, Stemmer}
import com.johnsnowlabs.nlp.annotators.pos.perceptron.PerceptronApproach
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetectorModel
import corenlp_simple.{SimplePosTagger, SimpleTokenizer}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._



object Test_NLP_Libraries {

  def Test_EnWiki(spark: SparkSession): Unit ={

    import spark.implicits._


    val inputFile="src/main/resources/enwikinews.json"

    val df = spark.read.format("json").option("mode", "DROPMALFORMED").load(inputFile)

    val newsDF = df
      .select("id", "title")
      .filter("id IS NOT NULL AND title IS NOT NULL")

    newsDF.count()

    //spark-nlp functions
    val documentAssembler = new DocumentAssembler()
      .setInputCol("title")
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
      .setCorpusPath("src/main/resources/anc-pos-corpus")
      .setNIterations(5)
      .setInputCols(Array("sentence", "token"))
      .setOutputCol("pos")

    val token_finisher = new Finisher()
      .setInputCols("stem")
      .setOutputCols("tokens_array")
      .setCleanAnnotations(true)
      .setOutputAsArray(true)

    val stopwords = spark.read.textFile("src/main/resources/stopwords_common_words_en.txt").collect()
    val filteredTokens = new StopWordsRemover()
      .setStopWords(stopwords)
      .setCaseSensitive(false)
      .setInputCol("tokens_array")
      .setOutputCol("filtered")

    //CoreNLP functions
    val corenlp_tokenizer = new SimpleTokenizer()
      .setInputCol("title")
      .setOutputCol("corenlp_tokens")

    val corenlp_pos = new SimplePosTagger()
      .setInputCol("title")
      .setOutputCol("corenlp_pos")

    val pipeline = new Pipeline()
      .setStages(Array(
        documentAssembler,
        sentenceDetector,
        regexTokenizer,
        normalizer,
        stemmer,
        corenlp_tokenizer,
        corenlp_pos,
        //        posTagger,
        token_finisher,
        filteredTokens
      ))

    val pipeLineDF = pipeline
      .fit(newsDF)
      .transform(newsDF)

    pipeLineDF.printSchema()
    pipeLineDF
      .select("title", "filtered")
      //      .select("token.result", "corenlp_tokens", "pos.result", "corenlp_pos")
      .show(20, truncate = false)

    val tokensDF = pipeLineDF
      .select(explode($"filtered").as("value"))
      .groupBy("value")
      .count
    //unique tokens
    println("number of unique tokens:")
    tokensDF.count()
    println("display top 500 tokens:")
    tokensDF.sort($"count".desc).show(500, truncate = false)

    spark.close()
  }
}