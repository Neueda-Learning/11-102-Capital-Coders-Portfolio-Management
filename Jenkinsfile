pipeline {
	agent any

	environment {
		COMPOSE_FILE = 'docker-compose.yml'
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		stage('Backend Tests') {
			steps {
				dir('backend') {
					script {
						if (isUnix()) {
							sh 'chmod +x mvnw'
							sh './mvnw clean test'
						} else {
							bat 'mvnw.cmd clean test'
						}
					}
				}
			}
		}

		stage('Build Images') {
			steps {
				script {
					if (isUnix()) {
						sh 'docker compose -f ${COMPOSE_FILE} build'
					} else {
						bat 'docker compose -f %COMPOSE_FILE% build'
					}
				}
			}
		}

		stage('Start Stack') {
			steps {
				script {
					if (isUnix()) {
						sh 'docker compose -f ${COMPOSE_FILE} up -d'
					} else {
						bat 'docker compose -f %COMPOSE_FILE% up -d'
					}
				}
			}
		}

		stage('Smoke Test API') {
			steps {
				script {
					if (isUnix()) {
						sh 'sleep 20'
						sh "curl --fail --silent --show-error http://localhost:8082/portfolios >/dev/null"
					} else {
						bat 'powershell -Command "Start-Sleep -Seconds 20"'
						bat 'powershell -Command "Invoke-WebRequest -UseBasicParsing http://localhost:8082/portfolios | Out-Null"'
					}
				}
			}
		}
	}

	post {
		always {
			script {
				if (isUnix()) {
					sh 'docker compose -f ${COMPOSE_FILE} down -v'
				} else {
					bat 'docker compose -f %COMPOSE_FILE% down -v'
				}
			}
		}
	}
}

