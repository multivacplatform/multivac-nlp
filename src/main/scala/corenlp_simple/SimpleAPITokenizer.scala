package corenlp_simple

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.types.{ArrayType, DataType, StringType}

class SimpleAPITokenizer(override val uid: String)
  extends UnaryTransformer[String, Seq[String], SimpleAPITokenizer]  {

  def this() = this(Identifiable.randomUID("CoreNLPTokenizer"))

  override protected def createTransformFunc: String => Seq[String] = {
    SimpleAPI_Functions.getWords _

  }

  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType)
  }

  override protected def outputDataType: DataType = {
    new ArrayType(StringType, true)
  }
}