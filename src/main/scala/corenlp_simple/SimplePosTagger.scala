package corenlp_simple

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.ml.param.Param
import org.apache.spark.sql.types.{ArrayType, DataType, StringType}

class SimplePosTagger(override val uid: String) extends UnaryTransformer[String, Seq[String], SimplePosTagger]  {

  def this() = this(Identifiable.randomUID("SimplePosTagger"))

  override protected def createTransformFunc: String => Seq[String] = {
    SimpleAPI_Functions.getPOS _
  }

  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType)
  }

  override protected def outputDataType: DataType = {
    new ArrayType(StringType, true)
  }
}