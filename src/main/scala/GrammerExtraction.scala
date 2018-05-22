import org.apache.spark.sql.functions.udf

object GrammerExtraction  extends Serializable {
  def posPhrases = udf((posArrays: Seq[String], tokenArrays: Seq[String]) => {
    posArrays.length + tokenArrays.length
  })
}