pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo "Compiling ..."
                sh "sbt compile"
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