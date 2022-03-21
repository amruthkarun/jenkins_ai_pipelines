pipeline {
    agent any
    stages {
        stage('Check Condition') {
            steps {
                echo 'Nexus setting up is done'
            }
        }
        stage('Checkout Source Code') {
            steps {
                git branch: "${params.source_code_branch}", credentialsId: "${params.github_token}", url: "${params.source_code_repository_url}"
                echo "Checkout source code done ${source_code_repository_url}"
            }
        }
        stage('install packages') {
            steps {
                sh 'conda activate'
                sh 'make clean'
                sh 'pip install -r requirements.txt'
            }
        }
        stage('Python lint') {
            steps {
                sh 'make lint'
            }
        }
        stage('Test & Coverage') {
            steps {
                sh 'make test'
                sh 'make coverage'
            }
        }
        stage('build package') {
            steps {
                sh 'make dist'
            }
        }
//     Set up the environment for twine upload artifacts, please read https://twine.readthedocs.io/en/stable/
        stage('release package') {
            environment {
                TWINE_REPOSITORY_URL = "${params.nexus_py_repository_url}"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: "${params.nexus_credential}", usernameVariable: 'TWINE_USERNAME', passwordVariable: 'TWINE_PASSWORD')]) {
                    echo "${TWINE_REPOSITORY_URL}"
                    echo "${TWINE_USERNAME}"
                    sh 'make release'
                    sh 'make clean'

                }
                withCredentials([usernamePassword(credentialsId: "${params.github_token}", passwordVariable: 'GIT_TOKEN')]) {
                    echo "${GIT_TOKEN}"
//                    sh('git push https://${GIT_TOKEN}@github.com/my-org/my-repo.git')
                }
            }
        }
    }
}
