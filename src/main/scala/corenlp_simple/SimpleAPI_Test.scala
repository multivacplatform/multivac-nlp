package corenlp_simple

import java.util.Properties

import scala.collection.JavaConverters._
import edu.stanford.nlp.simple._

object SimpleAPI_Test {

  def testEnglish(): Unit ={

    var wordsWithPosLabels: String = ""

    val document =
      """
        The historical origin of Japanese martial arts can be found in the warrior traditions of the samurai and the caste system that restricted the use of weapons by other members of society.
        Originally, samurai were expected to be proficient in many weapons, as well as unarmed combat, and attain the highest possible mastery of combat skills.
        Ordinarily, the development of combative techniques is intertwined with the tools used to execute those techniques.
        In a rapidly changing world, those tools are constantly changing, requiring that the techniques to use them be continuously reinvented. The history of Japan is somewhat unusual in its relative isolation. Compared with the rest of the world, the Japanese tools of war evolved slowly. Many people believe that this afforded the warrior class the opportunity to study their weapons with greater depth than other cultures. Nevertheless, the teaching and training of these martial arts did evolve. For example, in the early medieval period, the bow and the spear were emphasized, but during the Tokugawa period, fewer large scale battles took place, and the sword became the most prestigious weapon. Another trend that developed throughout Japanese history was that of increasing martial specialization as society became more stratified over time.
      """.stripMargin

    val wordsArray = new Sentence(document)
      .words()
      .asScala
      .zipWithIndex
      .map{case (k,v) => (v,k)}

    val postArray = new Sentence(document)
      .posTags()
      .asScala
      .zipWithIndex
      .map{case (k,v) => (v,k)}

    for (e <- postArray){
      wordsWithPosLabels +=  wordsArray.find(_._1 == e._1).get._2 + "_" + e._2 + " "
    }

    println("tokens: ", wordsArray)
    println("pos tags: ", postArray)
    println("words with pos labels: ", wordsWithPosLabels)

  }

  def testFrench(): Unit ={

    var frenchWordsWithPosLabels: String = ""

    val props = new Properties()
    props.setProperty("tokenize.language", "fr")
    props.setProperty("language", "french")
    props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/french/french.tagger")

    val frenchDocument =
      """
        L’allocation de Dirichlet latente (de l'anglais Latent Dirichlet Allocation) ou LDA est un modèle génératif probabiliste permettant d'expliquer des ensembles d'observations, par le moyen de groupes non observés, eux-mêmes définis par des similarités de données.
      """.stripMargin

    val wordsArrayFrench = new Sentence(frenchDocument, props)
      .words()
      .asScala
      .zipWithIndex
      .map{case (k,v) => (v,k)}

    val postArrayFrench = new Sentence(frenchDocument, props)
      .posTags(props)
      .asScala
      .zipWithIndex
      .map{case (k,v) => (v,k)}

    println("tokens: ", wordsArrayFrench)
    println("pos tags: ", postArrayFrench)

    val parseArrayFrench = new Sentence(frenchDocument, props)
      .parse(props)

    println("parsed document: ", parseArrayFrench)

    for (e <- postArrayFrench){
      frenchWordsWithPosLabels +=  wordsArrayFrench.find(_._1 == e._1).get._2 + "_" + e._2 + " "
    }

    println("french pos tags: ", frenchWordsWithPosLabels)

  }
}
/*
static {
    //See http://www.lattice.cnrs.fr/sites/itellier/SEM.html
    MAPPING.put("A", "ADJ"); //adjective
    MAPPING.put("ADJ", "ADJ");
    MAPPING.put("ADJWH", "ADJ"); //Interrogative adjective
    MAPPING.put("ADV", "ADV"); //adverb
    MAPPING.put("ADVWH", "ADV"); //Interrogative adverb
    MAPPING.put("C", "CONJ"); //conjunction and subordinating conjunction
    MAPPING.put("CC", "CONJ"); //conjunction and subordinating conjunction
    MAPPING.put("CL", "PRON"); //weak clitic pronoun TODO: ADP?
    MAPPING.put("CLO", "PRON"); //subject clitic pronoun TODO
    MAPPING.put("CLR", "PRON"); //reflexive clitic pronoun TODO
    MAPPING.put("CLS", "PRON"); //object clitic pronoun TODO
    MAPPING.put("CS", "SCONJ"); //subordinating conjunction
    MAPPING.put("D", "DET"); //determiner
    MAPPING.put("DET", "DET"); //determiner
    MAPPING.put("DETWH", "DET"); //interrogative determiner
    MAPPING.put("ET", "X"); //foreign word
    MAPPING.put("I", "INTJ"); //interjection
    MAPPING.put("N", "NOUN"); //noun
    MAPPING.put("NC", "NOUN"); //noun
    MAPPING.put("NP", "PROPN"); //proper noun
    MAPPING.put("NPP", "PROPN"); //pfoper noun
    MAPPING.put("P", "ADP"); // preposition
    MAPPING.put("PREF", "PART" ); //prefix
    MAPPING.put("PRO", "PRON"); //strong pronoun
    MAPPING.put("PROREL", "PRON"); //relative pronoun
    MAPPING.put("PROWH", "PRON"); //interrogative pronoun
    MAPPING.put("V", "VERB"); // verb
    MAPPING.put("VINF", "NOUN"); //infinitive verb TODO: good tag?
    MAPPING.put("VIMP", "VERB"); //imperative verb
    MAPPING.put("VPP", "ADJ"); //past participle verb TODO: good tag?
    MAPPING.put("VPR", "VERB"); //present participle verb TODO: good tag?
    MAPPING.put("VS", "VERB"); //subjonctive verb
    MAPPING.put("PUNC", "PUNCT"); // punctuation
    MAPPING.put(".$$.", "PUNCT");
  }
*/