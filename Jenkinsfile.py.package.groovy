pipeline {
    environment{
        PATH = "$PATH:/home/jenkins/.local/bin"
    }
    agent {
        label 'python'
    }
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
                withPythonEnv('python3') {
                    sh 'python3 -m pip install --user --upgrade pip'
                    sh 'make clean'
                    sh 'pip install -r requirements.txt'
                }
            }
        }
        stage('Python lint') {
            steps {
                withPythonEnv('python3') {
                sh 'make lint'
                }
            }
        }
        stage('Test & Coverage') {
            steps {
                withPythonEnv('python3') {
                    sh 'make test'
                    sh 'make coverage'
                }
            }
        }
        stage('build package') {
            steps {
                withPythonEnv('python3') {
                    sh 'make dist'
                }
            }
        }
//     Set up the environment for twine upload artifacts, please read https://twine.readthedocs.io/en/stable/
        stage('release package') {
            environment {
                TWINE_REPOSITORY_URL = "${params.nexus_py_repository_url}"
            }
            steps {
                withPythonEnv('python3') {
                    withCredentials([usernamePassword(credentialsId: "${params.nexus_credential}", usernameVariable: 'TWINE_USERNAME', passwordVariable: 'TWINE_PASSWORD')]) {
                        echo "${TWINE_REPOSITORY_URL}"
                        echo "${TWINE_USERNAME}"
                        sh 'make release'
                        sh 'make clean'
                    }
                }
            }
        }
    }
}
