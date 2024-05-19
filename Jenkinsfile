pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        SECRET_NAME = 'youtuber_prod_app'
    }

    stages {
        stage('Setup AWS CLI') {
            steps {
                script {
                    if (sh(script: 'which aws', returnStatus: true) != 0) {
                        sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                        sh 'unzip awscliv2.zip'
                        sh 'sudo ./aws/install'
                    }
                }
            }
        }

        stage('Fetch Secrets') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
                 credentialsId: 'aws-credentials-id']]) {
                    script {
                        def secretJson = sh(
                            script: """
                                aws secretsmanager get-secret-value --region ${AWS_REGION} --secret-id ${SECRET_NAME} --query SecretString --output text
                            """,
                            returnStdout: true
                        ).trim()

                        def secretMap = readJSON text: secretJson

                        env.mysql_rds_pw = secretMap.mysql_rds_pw
                        env.openai_api_key = secretMap.openai_api_key
                        env.youtube_api_key = secretMap.youtube_api_key
                        env.mysql_rds_id = secretMap.mysql_rds_id
                        env.kafka_connection_ip = secretMap.kafka_connection_ip
                        env.mysql_rds_url = secretMap.mysql_rds_url
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                # 기존 컨테이너 중지 및 삭제
                docker ps -a -q --filter "name=spring-boot-app" | grep -q . && docker stop spring-boot-app && docker rm spring-boot-app | true

                # 기존 이미지 삭제
                docker rmi spring-boot-app:imgfile | true

                # 새 이미지 생성
                docker build -t spring-boot-app:imgfile .
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                # 새 이미지로 컨테이너 실행
                docker run -d -p 8080:8080 --name spring-boot-app \
                -e AWS_REGION=$AWS_REGION \
                -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
                -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
                -e mysql_rds_pw=$mysql_rds_pw \
                -e openai_api_key=$openai_api_key \
                -e youtube_api_key=$youtube_api_key \
                -e mysql_rds_id=$mysql_rds_id \
                -e kafka_connection_ip=$kafka_connection_ip \
                -e mysql_rds_url=$mysql_rds_url \
                spring-boot-app:imgfile
                '''
            }
        }
    }
}
