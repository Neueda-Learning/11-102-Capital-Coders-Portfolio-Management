pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        COMPOSE_CMD_FILE = '.compose_cmd'
        ENV_FILE = '.env'
        APP_PORT = '0'
        BACKEND_PORT = '0'
        MYSQL_PORT = '3306'
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Validate Tooling') {
            steps {
                script {
                    if (!isUnix()) {
                        error('This pipeline requires a Linux Jenkins agent with docker, curl, and docker compose/docker-compose.')
                    }

                    sh 'docker --version'
                    sh 'curl --version'

                    def composeCmd = sh(
                            script: '''
if docker compose version >/dev/null 2>&1; then
    echo "docker compose"
elif docker-compose version >/dev/null 2>&1; then
    echo "docker-compose"
fi
''',
                            returnStdout: true
                    ).trim()

                    if (!composeCmd) {
                        error('Neither docker compose nor docker-compose is available on this Jenkins agent.')
                    }

                    writeFile file: env.COMPOSE_CMD_FILE, text: composeCmd + "\n"
                    sh "${composeCmd} version"
                }
            }
        }

        stage('Build Backend') {
            steps {
                sh 'chmod +x backend/mvnw'
                sh './backend/mvnw -B -f backend/pom.xml clean package -DskipTests'
            }
        }

        stage('Prepare Deploy Env') {
            steps {
                script {
                    def twelveDataKey = (env.TWELVE_DATA_API_KEY ?: '').trim()
                    def newsApiKey = (env.NEWS_API_KEY ?: '').trim()

                    if (!twelveDataKey) {
                        echo 'TWELVE_DATA_API_KEY is not configured. Live market price endpoints will return validation errors until it is supplied.'
                    }

                    if (!newsApiKey) {
                        echo 'NEWS_API_KEY is not configured. News endpoints will return validation errors until it is supplied.'
                    }

                    def envContent = """
MYSQL_ROOT_PASSWORD=${env.MYSQL_ROOT_PASSWORD ?: 'root123'}
MYSQL_DATABASE=${env.MYSQL_DATABASE ?: 'portfolio_management'}
MYSQL_USER=${env.MYSQL_USER ?: 'portfolio_user'}
MYSQL_PASSWORD=${env.MYSQL_PASSWORD ?: 'portfolio_password'}
MYSQL_PORT=${env.MYSQL_PORT ?: '3306'}
APP_PORT=${env.APP_PORT ?: '0'}
BACKEND_PORT=${env.BACKEND_PORT ?: '0'}
TWELVE_DATA_BASE_URL=${env.TWELVE_DATA_BASE_URL ?: 'https://api.twelvedata.com'}
TWELVE_DATA_API_KEY=${twelveDataKey}
TWELVE_DATA_CACHE_MS=${env.TWELVE_DATA_CACHE_MS ?: '120000'}
NEWS_API_BASE_URL=${env.NEWS_API_BASE_URL ?: 'https://newsapi.org/v2'}
NEWS_API_KEY=${newsApiKey}
NEWS_API_CACHE_MS=${env.NEWS_API_CACHE_MS ?: '300000'}
""".trim() + "\n"

                    writeFile file: env.ENV_FILE, text: envContent
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def composeCmd = readFile(env.COMPOSE_CMD_FILE).trim()
                    sh "${composeCmd} --env-file .env down --remove-orphans || true"
                    sh "${composeCmd} --env-file .env up -d --build --remove-orphans"
                    sh "${composeCmd} --env-file .env ps"
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    def composeCmd = readFile(env.COMPOSE_CMD_FILE).trim()
                    def backendPort = sh(
                            script: """
${composeCmd} --env-file .env port backend 8080 | tail -n 1 | sed -E 's/.*:([0-9]+)/\\1/'
""",
                            returnStdout: true
                    ).trim()

                    if (!backendPort) {
                        error('Unable to resolve published backend port from docker compose.')
                    }

                    def appPort = sh(
                            script: """
${composeCmd} --env-file .env port frontend 80 | tail -n 1 | sed -E 's/.*:([0-9]+)/\\1/'
""",
                            returnStdout: true
                    ).trim()

                    if (!appPort) {
                        error('Unable to resolve published frontend port from docker compose.')
                    }

                    sh """
for i in \$(seq 1 30); do
  code=\$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${backendPort}/v3/api-docs" || true)
  if [ "\$code" = "200" ]; then
    echo "Backend health endpoint is ready (HTTP \$code)."
    exit 0
  fi
  echo "Waiting for backend readiness... (\$i/30), last code=\$code"
  sleep 5
done
echo "Backend did not become ready in time."
exit 1
"""

                    sh "curl -fsSL http://localhost:${appPort}/ >/dev/null"
                    sh "curl -fsS http://localhost:${appPort}/login/login.html >/dev/null"
                    sh "${composeCmd} --env-file .env ps"
                }
            }
        }
    }

    post {
        always {
            script {
                def composeCmd = fileExists(env.COMPOSE_CMD_FILE) ? readFile(env.COMPOSE_CMD_FILE).trim() : ''
                if (composeCmd) {
                    sh "${composeCmd} --env-file .env ps || true"
                    sh "${composeCmd} --env-file .env logs --tail=150 || true"
                }
            }
            sh 'rm -f .env .compose_cmd || true'
        }
        success {
            echo 'Deployment pipeline completed successfully.'
        }
        failure {
            echo 'Deployment pipeline failed. Review container logs above.'
        }
    }
}