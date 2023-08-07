pipeline{
    agent {
        kubernetes{
            yaml '''
               apiVersoin: v1
               kind: Pod
               spec:
                 serviceAccountName: jenkins
                 containers:
                 - name: aws
                   image: amazon/aws-cli
                   command:
                   - sleep
                   args:
                   - infinity
                 - name: gradle
                   image: gradle:8.1.1
                   command: ['sleep']
                   args: ['infinity']
                 - name: kaniko
                   image: gcr.io/kaniko-project/executor:debug
                   command:
                   - sleep
                   args:
                   - infinity
                   env:
                   - name: AWS_SDK_LOAD_CONFIG
                     value: true
            '''
        }
    }
    stages{
        stage('Git Clone'){
            steps{
                git url: 'https://github.com/SWM-YouQuiz/Authentication-Service.git',
                    branch: 'dev',
                    credentialsId: "github_personal_access_token"
            }
        }
        stage('Gradle Build'){
            steps{
                container('gradle'){
                    sh 'mkdir ./src/main/resources/static'
                    sh 'mkdir ./src/main/resources/static/docs'

                    sh 'gradle build'

                    sh 'mv ./build/libs/auth-service.jar ./'
                }
            }
        }
        stage('aws'){
            steps{
                container('aws'){
                    sh "aws s3 cp src/main/resources/static/docs/api.yml s3://quizit-swagger/auth.yml"
                }
            }
        }
        stage('Docker Build'){
            steps{
                container('kaniko'){
                    sh "executor --dockerfile=Dockerfile --context=dir://${env.WORKSPACE} --destination=${env.ECR_AUTH_SERVICE}:latest"
                }
            }
        }
    }
}