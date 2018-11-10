pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo "Compiling..."
                sh "${tool name: 'sbt', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin/sbt compile"
                echo "Compiled"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                echo 'No test at this moment'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Packaging....'
                sh "${tool name: 'sbt', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin/sbt package"
                echo 'Successfully packaged'
            }
        }
    }
}