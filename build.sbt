name := "multivac-nlp"
organization := "fr.iscpif.multivac"

version := "1.1.0"

scalaVersion := "2.11.12"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

//resolvers ++= Seq(
//  "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
//  "Spray Repository" at "http://repo.spray.cc/",
//  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
//  "Akka Repository" at "http://repo.akka.io/releases/",
//  "Twitter4J Repository" at "http://twitter4j.org/maven2/",
//  "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
//  "Twitter Maven Repo" at "http://maven.twttr.com/",
//  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
//  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
//  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
//  "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
//  "Apache Repository" at "https://repository.apache.org/content/repositories/releases/",
//  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
//  Resolver.sonatypeRepo("public")
//)

libraryDependencies ++= {
  val sparkVer = "2.3.2"
  val corenlpVer = "3.9.2"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer,
    "org.apache.spark" %% "spark-sql" % sparkVer,
    "org.apache.spark" %% "spark-streaming" % sparkVer,
    "org.apache.spark" %% "spark-mllib" %sparkVer,
    "org.apache.spark" %% "spark-hive" % sparkVer,
    "org.apache.spark" %% "spark-graphx" % sparkVer,
    "org.apache.spark" %% "spark-yarn" % sparkVer,
    "com.typesafe" % "config" % "1.3.1",
    "com.johnsnowlabs.nlp" %% "spark-nlp" % "1.7.2",
    "edu.stanford.nlp" % "stanford-corenlp" % corenlpVer,
    "edu.stanford.nlp" % "stanford-corenlp" % corenlpVer classifier "models",
    "edu.stanford.nlp" % "stanford-corenlp" % corenlpVer classifier "models-french",
    "com.optimaize.languagedetector" % "language-detector" % "0.6",
    "com.spotify" % "docker-client" % "3.5.13"
  )
}

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
