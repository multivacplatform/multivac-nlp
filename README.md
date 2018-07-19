# multivac-nlp [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/multivacplatform/es-punchcard/blob/master/LICENSE.md) [![Build Status](https://travis-ci.org/multivacplatform/multivac-nlp.svg?branch=master)](https://travis-ci.org/multivacplatform/multivac-nlp) [![multivac discuss](https://img.shields.io/badge/multivac-discuss-ff69b4.svg)](https://discourse.iscpif.fr/c/multivac) [![multivac channel](https://img.shields.io/badge/multivac-chat-ff69b4.svg)](https://chat.iscpif.fr/channel/multivac)

Testing and benchmarking some of the existing NLP libraries in Apache Spark

## NLP libraries used in Multivac-NLP
### spark-nlp
latest version: [https://github.com/JohnSnowLabs/spark-nlp]()
### Stanford-CoreNLP
CoreNLP 3.7: [https://github.com/stanfordnlp/CoreNLP]()


##Functions
### Word2Vec
Training Spark ML Word2Vec:

#### Wiki News

* articles 19750
* unique tokens: 145349
* total tokens: 8070537

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
#### French Political Tweets
* tweets: 18716
* unique tokens: 53268
* total tokens: 385948

```
findSynonyms: Lundi

+--------+------------------+
|word    |similarity        |
+--------+------------------+
|dcembre |0.5641320943832397|
|mercredi|0.5640853643417358|
|vendredi|0.5532589554786682|
|samedi  |0.5358499884605408|
+--------+------------------+
```



## Environment

* Spark 2.2 Local / IntelliJ
* Spark 2.2 / Cloudera CDH 5.13 / YARN (cluster - client)

## Code of Conduct

This, and all github.com/multivacplatform projects, are under the [Multivac Platform Open Source Code of Conduct](https://github.com/multivacplatform/code-of-conduct/blob/master/code-of-conduct.md). Additionally, see the [Typelevel Code of Conduct](http://typelevel.org/conduct) for specific examples of harassing behavior that are not tolerated.

## Copyright and License

Code and documentation copyright (c) 2017 [ISCPIF - CNRS](http://iscpif.fr). Code released under the [MIT license](https://github.com/multivacplatform/multivac-nlp/blob/master/LICENSE).
