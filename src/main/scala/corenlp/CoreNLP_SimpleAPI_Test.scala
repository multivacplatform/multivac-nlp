package corenlp

import java.util.Properties
import scala.collection.JavaConverters._
import edu.stanford.nlp.simple._

object CoreNLP_SimpleAPI_Test {

  def test(): Unit ={
    val props = new Properties()
    props.setProperty("tokenize.language", "fr")


    val document =
      """
        The historical origin of Japanese martial arts can be found in the warrior traditions of the samurai and the caste system that restricted the use of weapons by other members of society.
        Originally, samurai were expected to be proficient in many weapons, as well as unarmed combat, and attain the highest possible mastery of combat skills.
        Ordinarily, the development of combative techniques is intertwined with the tools used to execute those techniques.
        In a rapidly changing world, those tools are constantly changing, requiring that the techniques to use them be continuously reinvented. The history of Japan is somewhat unusual in its relative isolation. Compared with the rest of the world, the Japanese tools of war evolved slowly. Many people believe that this afforded the warrior class the opportunity to study their weapons with greater depth than other cultures. Nevertheless, the teaching and training of these martial arts did evolve. For example, in the early medieval period, the bow and the spear were emphasized, but during the Tokugawa period, fewer large scale battles took place, and the sword became the most prestigious weapon. Another trend that developed throughout Japanese history was that of increasing martial specialization as society became more stratified over time.
      """.stripMargin
    val newSentences = new Document(document).sentences(props).asScala.map(_.text())
    val postArray = new Sentence(document, props).posTags(props)

    println("sentences: ", newSentences)
    println("pos tags", postArray)
  }
}
