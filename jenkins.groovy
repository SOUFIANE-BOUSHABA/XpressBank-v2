pipeline {
    agent {
        docker {
            image 'maven:3.8.8-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        GIT_REPOSITORY = "https://github.com/SOUFIANE-BOUSHABA/XpressBank-v2.git"
        SONAR_PROJECT_KEY = "com.example:version2_XpresBank"
        SONAR_HOST_URL = "http://host.docker.internal:9000"
        SONAR_TOKEN = "sqa_066a8f4cc7cf0cf407e56440b6014414a80e6548"
        DOCKER_IMAGE_NAME = "springboot-app"
        RECIPIENT_EMAIL = "soufianboushaba12@gmail.com"
        POSTGRES_CONTAINER_NAME = "XpressBank-devops"
        SONAR_CONTAINER_NAME = "XpressBank-sonarqube"
        POSTGRES_HOST = "host.docker.internal"
        POSTGRES_PORT = "5433"
        POSTGRES_DB = "XpressBank"
        POSTGRES_USER = "admin"
        POSTGRES_PASSWORD = "admin"
    }

    stages {
        stage('Install Tools') {
            steps {
                script {
                    echo "Installing necessary tools..."
                    sh '''
                    apt-get update && apt-get install -y docker.io curl jq unzip
                    curl -o /tmp/sonar-scanner-cli.zip -L https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.8.0.2856-linux.zip
                    unzip /tmp/sonar-scanner-cli.zip -d /opt
                    ln -s /opt/sonar-scanner-4.8.0.2856-linux/bin/sonar-scanner /usr/local/bin/sonar-scanner
                    '''
                }
            }
        }

        stage('Check Existing Containers') {
            steps {
                script {
                    echo "Checking if PostgreSQL and SonarQube containers are running..."
                    def postgresStatus = sh(script: "docker ps -q -f name=${POSTGRES_CONTAINER_NAME}", returnStdout: true).trim()
                    if (!postgresStatus) {
                        echo "Starting PostgreSQL container..."
                        sh "docker start ${POSTGRES_CONTAINER_NAME}"
                    } else {
                        echo "PostgreSQL container is already running."
                    }

                    def sonarStatus = sh(script: "docker ps -q -f name=${SONAR_CONTAINER_NAME}", returnStdout: true).trim()
                    if (!sonarStatus) {
                        echo "Starting SonarQube container..."
                        sh "docker start ${SONAR_CONTAINER_NAME}"
                    } else {
                        echo "SonarQube container is already running."
                    }
                }
            }
        }

        stage('Checkout Code') {
            steps {
                echo "Checking out code from GitHub..."
                git branch: 'master', url: "${GIT_REPOSITORY}"
            }
        }

        stage('Build and Run Tests') {
            steps {
                echo "Building the project and running tests..."
                sh '''
                mvn clean package test -Dspring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} \
                                       -Dspring.datasource.username=${POSTGRES_USER} \
                                       -Dspring.datasource.password=${POSTGRES_PASSWORD}
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis..."
                sh """
                sonar-scanner \
                  -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                  -Dsonar.sources=src \
                  -Dsonar.java.binaries=target/classes \
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                  -Dsonar.host.url=${SONAR_HOST_URL} \
                  -Dsonar.login=${SONAR_TOKEN}
                """
            }
        }

        stage('Quality Gate Check') {
            steps {
                script {
                    echo "Checking SonarQube Quality Gate..."
                    def qualityGate = sh(
                            script: """
                        curl -s -u "${SONAR_TOKEN}:" \
                        "${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=${SONAR_PROJECT_KEY}" \
                        | jq -r '.projectStatus.status'
                        """,
                            returnStdout: true
                    ).trim()
                    if (qualityGate != "OK") {
                        error "Quality Gate failed! Stopping the build."
                    } else {
                        echo "Quality Gate passed! Proceeding..."
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image..."
                sh '''
                JAR_FILE=$(ls target/version2_XpresBank-*.jar | head -n 1)
                if [ -z "$JAR_FILE" ]; then
                  echo "JAR file not found in target directory! Exiting..."
                  exit 1
                fi
                docker build --build-arg JAR_FILE=$JAR_FILE -t ${DOCKER_IMAGE_NAME} .
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                echo "Deploying Docker container..."
                sh """
                docker stop ${DOCKER_IMAGE_NAME}-container || true
                docker rm ${DOCKER_IMAGE_NAME}-container || true
                docker run -d -p 8080:8181 --name ${DOCKER_IMAGE_NAME}-container ${DOCKER_IMAGE_NAME}
                """
            }
        }
    }

    post {
        success {
            echo "Build succeeded! Sending success notifications..."
            mail to: "${RECIPIENT_EMAIL}",
                    subject: "✅ SUCCESS: Build #${env.BUILD_NUMBER} - ${env.JOB_NAME}",
                    body: "The build and deployment were successful.\n\nDetails: ${env.BUILD_URL}"
        }
        failure {
            echo "Build failed! Sending failure notifications..."
            mail to: "${RECIPIENT_EMAIL}",
                    subject: "❌ FAILURE: Build #${env.BUILD_NUMBER} - ${env.JOB_NAME}",
                    body: "The build or deployment failed.\n\nDetails: ${env.BUILD_URL}"
        }
    }
}