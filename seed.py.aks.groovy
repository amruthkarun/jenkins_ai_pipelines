//github server setting
String github_token_credential = "git-token-credentials"
String github_host = "github.com"

//central pipeline repository
String pipeline_repository = "wuqunfei/jenkins_ai_pipelines"
String pipeline_jenkins_file = "Jenkinsfile.py.aks.groovy"

//application source code
String source_code_repository_url = "https://github.com/wuqunfei/ocr_service"
String source_code_branch = "main"

//deploy source code
String deploy_code_repository_url = "https://github.com/wuqunfei/jenkins_ai_deployment"
String deploy_code_branch = "main"

//ACR
String acr_name = "prodaidevopsazverpjq"
String acr_credential = "acr_credential"

//Application
String application_name = "pysimple"

pipelineJob("ocr-service-builder") {
    parameters {

        stringParam('github_token_credential', github_token_credential, 'Github token credential id')

        stringParam("application_name", application_name, "application_name for docker image")
        stringParam("source_code_repository_url", source_code_repository_url, "Application Source Code HTTP URL")
        stringParam("source_code_branch", source_code_branch, "Application Source Code Branch, default main")


        stringParam("deploy_code_repository_url", deploy_code_repository_url, "Helm/K8S deployment repository")
        stringParam("deploy_code_branch", deploy_code_branch, "default branch of deployment repository")


        stringParam("pipeline_repository", pipeline_repository, "pipeline github project name")
        stringParam("pipeline_jenkins_file", pipeline_jenkins_file, 'pipeline file')


        stringParam("acr_name", acr_name, "Azure Container Registry name for docker image")
        stringParam("acr_credential", acr_credential, "Azure Container credential id in jenkins user/pwd")


    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github(pipeline_repository, "https", github_host)
                        credentials(github_token_credential)
                    }
                }
            }
            scriptPath(pipeline_jenkins_file)
        }
    }
}