void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/f-lab-edu/eat-enjoy"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/eat-enjoy"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

pipeline {
    agent any

    environment {
        PATH = "/usr/lib/gradle/bin:$PATH"
    }

    stages {
        stage('Git Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'gradle clean build --exclude-task test'
            }
        }

        stage('Test') {
            steps {
                sh 'gradle test'
                junit '**/build/test-results/test/*.xml'
            }
        }
    }

    post {
        success {
            setBuildStatus("Build succeeded", "SUCCESS");
        }
        failure {
            setBuildStatus("Build failed", "FAILURE");
        }
    }
}
