pipeline {
    agent {
        docker {
            label 'docker-agent'
            image 'hseeberger/scala-sbt'
        }

    }
    stages {
        stage('Build') {
            steps {
                echo 'Compiling ...'
                sh 'sbt compile'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}