pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Compile..'
                sh 'sbt --version'
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