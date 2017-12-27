# multivac-nlp
Testing and benchmarking some of the existing NLP libraries in Apache Spark

## NLP libraries used in Multivac-NLP
### spark-nlp
latest version: [https://github.com/JohnSnowLabs/spark-nlp]()
### Stanford-CoreNLP
CoreNLP 3.7: [https://github.com/stanfordnlp/CoreNLP]()


##Functions
### Word2Vec
Training Spark ML Word2Vec by WikiNews (144833 tokes after stop words)

```
findSynonyms: London

+----------+------------------+
|word      |similarity        |
+----------+------------------+
|cologne   |0.8254308104515076|
|glasgow   |0.820296585559845 |
|londons   |0.7977003455162048|
|birmingham|0.7859082818031311|
+----------+------------------+

findSynonyms: France

+-----------+------------------+
|word       |similarity        |
+-----------+------------------+
|leipheimer |0.8519462943077087|
|levi       |0.8423983454704285|
|spain      |0.8412571549415588|
|netherlands|0.8346607685089111|
+-----------+------------------+

findSynonyms: Monday

+---------+------------------+
|word     |similarity        |
+---------+------------------+
|thursday |0.9608868956565857|
|wednesday|0.9582304954528809|
|friday   |0.951666533946991 |
|tuesday  |0.9416679739952087|
+---------+------------------+
```


## Environment
* Spark 2.2 Local / IntelliJ
* Spark 2.2 / Cloudera CDH 5.13 / YARN (cluster - client)

## Code of Conduct

This, and all github.com/multivacplatform projects, are under the [Multivac Platform Open Source Code of Conduct](https://github.com/multivacplatform/code-of-conduct/blob/master/code-of-conduct.md). Additionally, see the [Typelevel Code of Conduct](http://typelevel.org/conduct) for specific examples of harassing behavior that are not tolerated.

## Copyright and License

Code and documentation copyright 2017 [ISCPIF - CNRS](http://iscpif.fr). Code released under the [MIT license](https://github.com/multivacplatform/multivac-nlp/blob/master/LICENSE).
