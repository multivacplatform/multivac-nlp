package corenlp_simple

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.types.{ArrayType, DataType, StringType}

class FrenchSimplePosTagger(override val uid: String) extends UnaryTransformer[String, Seq[String], FrenchSimplePosTagger]  {

  def this() = this(Identifiable.randomUID("SimplePosTagger"))

  override protected def createTransformFunc: String => Seq[String] = {
    SimpleAPI_Functions.getPOSFrench _
  }

  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType)
  }

  override protected def outputDataType: DataType = {
    new ArrayType(StringType, true)
  }
}