pipeline {
	agent any

	environment {
		DATABASE_USER = "user"
		DATABASE_PASSWORD = "123"
		DATABASE_NAME = "gorzdrav_bot"
		BOT_NAME = "NameOfYourBot"
		BOT_TOKEN = "12334566:SDfsdfdsfdsfsdfsdFDSfdsfdsfsdf"
		LOKI_URL = "http://localhost:3100"
		LOKI_AUTH = "username"
		LOKI_PASSWORD = "password"
		AUTH_USERNAME = "admin"
		AUTH_PASSWORD = "password"
	}

	stages {
		stage('Checkout') {
			steps {
				git branch: 'master', url: 'https://github.com/elisevgeniy/GorzdravBot.git'
			}
		}

		stage('Build'){
			steps {
				dir("build") {
					sh "docker compose build"
				}
			}
		}

		stage('Run'){
			steps {
				dir("build") {
					sh "docker compose up -d"
				}
			}
		}
	}
}