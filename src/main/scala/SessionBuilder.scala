import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object SessionBuilder {
  def buildSession(env: String): SparkSession = {

    var sparkMaster = ConfigFactory.load().getString("spark.local.master.value")
    if(env == "prod")
      sparkMaster = ConfigFactory.load().getString("spark.prod.master.value")

    val spark: SparkSession = SparkSession.builder
      .appName("multivac-nlp")
      .master(sparkMaster)
      .enableHiveSupport()
      .config("spark.driver.memory", "4G")
      .config("spark.kryoserializer.buffer.max","200M")
      .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
      .getOrCreate

    spark.sparkContext.setLogLevel("INFO")

    spark
  }
}
