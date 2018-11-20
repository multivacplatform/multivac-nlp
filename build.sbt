import sbtassembly.MergeStrategy
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaServerAppPackaging)
enablePlugins(JavaAppPackaging)

val sparkVer = "2.4.0"
val corenlpVer = "3.9.2"
val hadoopVer = "2.7.2"
val scalaTestVer = "3.0.0"
val sparknlpVer = "1.7.2"

lazy val commonSettings = Seq(
  name := "multivac-nlp",
  organization := "fr.iscpif.multivac",
  version := "1.1.0",
  scalaVersion := "2.11.12",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))
)


lazy val analyticsDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVer % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVer % "provided"
)

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVer % "test"
)

lazy val utilDependencies = Seq(
  "com.johnsnowlabs.nlp" %% "spark-nlp" % "1.7.2",
  "edu.stanford.nlp" % "stanford-corenlp" % corenlpVer,
  "edu.stanford.nlp" % "stanford-corenlp" % corenlpVer classifier "models",
  "edu.stanford.nlp" % "stanford-corenlp" % corenlpVer classifier "models-french"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      analyticsDependencies ++
        testDependencies ++
        utilDependencies
  )


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {
    j => {
      j.data.getName.startsWith("spark-core") ||
        j.data.getName.startsWith("spark-sql") ||
        j.data.getName.startsWith("spark-hive") ||
        j.data.getName.startsWith("spark-mllib") ||
        j.data.getName.startsWith("spark-graphx") ||
        j.data.getName.startsWith("spark-yarn") ||
        j.data.getName.startsWith("spark-streaming") ||
        j.data.getName.startsWith("hadoop")
    }
  }
}
