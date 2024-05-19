pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        SECRET_NAME = 'youtuber_prod_app' // Secrets Manager에서 저장된 비밀의 이름
    }

    stages {
        stage('Setup AWS CLI') {
            steps {
                script {
                    // AWS CLI가 설치되어 있는지 확인하고 설치되지 않았다면 설치합니다.
                    if (sh(script: 'which aws', returnStatus: true) != 0) {
                        sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                        sh 'unzip awscliv2.zip'
                        sh 'sudo ./aws/install'
                    }
                }
            }
        }

        stage('Debug AWS Credentials') {
            steps {
                script {
                    // 환경 변수가 올바르게 설정되었는지 확인합니다.
                    sh 'aws configure list'
                }
            }
        }

        stage('Fetch Secrets') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-credentials-id'
                ]]) {
                    script {
                        // AWS CLI를 사용하여 AWS 환경 변수를 설정합니다.
                        sh """
                            export AWS_REGION=${AWS_REGION}
                        """

                        // AWS Secrets Manager에서 비밀을 가져와 환경 변수로 설정합니다.
                        def secretJson = sh(
                            script: """
                                aws secretsmanager get-secret-value --region ${AWS_REGION} --secret-id ${SECRET_NAME} --query SecretString --output text
                            """,
                            returnStdout: true
                        ).trim()

                        def secretMap = readJSON text: secretJson

                        // 가져온 비밀을 환경 변수로 설정합니다.
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
                sh './gradlew build'
            }
        }

        stage('Deploy') {
            steps {
                sh './gradlew bootRun'
            }
        }
    }
}
