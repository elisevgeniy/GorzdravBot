# 🏥 GorzdravBot | Автоматизация записи к врачу

<!--Блок информации о репозитории в бейджах-->
![Static Badge](https://img.shields.io/badge/elisevgeniy-GorzdravBot-GorzdravBot)
[![Build Status](https://img.shields.io/github/actions/workflow/status/elisevgeniy/GorzdravBot/build.yml?branch=master)](https://github.com/elisevgeniy/GorzdravBot/actions)
![GitHub top language](https://img.shields.io/github/languages/top/elisevgeniy/GorzdravBot)
![GitHub](https://img.shields.io/github/license/elisevgeniy/GorzdravBot)
![GitHub issues](https://img.shields.io/github/issues/elisevgeniy/GorzdravBot)
![GitHub Repo stars](https://img.shields.io/github/stars/elisevgeniy/GorzdravBot)

Умный бот на Java для автоматизации записи к врачу через сервис «Горздрав».
Получайте уведомления о свободных номерках и автоматизируйте процесс записи.

Возможности бота описаны в разделе [Как работает бот](#-как-работает-бот)

## 🌟 Особенности

- 🕵️‍♂️ Постоянный мониторинг номерков
- 🔔 Уведомления через Telegram
- ⚡ Автоматическая запись при обнаружении номерков

## 📦 Требования

- Java 21+
- Apache Maven 3.8+
- Postgres 17+
- Telegram бот ([регистрация бота](#-регистрация-бота))

## 💬 Регистрация бота

Для работы бота необходимо его зарегистрировать и получить `name` и `token` бота.

Подробности на [оф. сайте](https://core.telegram.org/bots/features#creating-a-new-bot).

## 🚀 Запуск

Есть 3 варианта запуска: с помощью [Maven](#-с-помощью-maven), [JAR файла](#-с-помощью-jar-файла), 
[Docker Run](#-с-помощью-docker-docker-run) и [Docker compose](#-с-помощью-docker-docker-compose).

### 🔨 С помощью Maven

1. Скопировать репозиторий
   ```
   git clone https://github.com/elisevgeniy/GorzdravBot.git
   cd GorzdravBot
   ```
2. Получить `username`, `password` и `database name` от запущенной PostgreSQL базы данных
3. Зполнить файл `src/main/resources/application.yaml` вышеуказанными параметрами
4. Запустить бота ```mvnw spring-boot:run```

### 🐳 С помощью Docker (docker run)

1. Получить `username`, `password` и `database name` от запущенной PostgreSQL базы данных
2. Заполнить параметры и запустить приложение с помощью cmd на windows (для запуска с помощью bash на linux замените `^` на `\`).<br>
   Обратите внимание, что сеть `--net` должна обеспечивать доступность базы данных<br>
   ``` 
    java ^
    -e DATABASE_URL="localhost:5432" ^
    -e DATABASE_USER="user" ^
    -e DATABASE_PASSWORD="123" ^
    -e DATABASE_NAME="gorzdrav_bot" ^
    -e BOT_NAME="NameOfYourBot" ^
    -e BOT_TOKEN="12334566:SDfsdfdsfdsfsdfsdFDSfdsfdsfsdf" ^
    -e LOKI_URL="http://localhost:3100" ^
    -e LOKI_AUTH="username" ^
    -e LOKI_PASSWORD="password" ^
    -e AUTH_USERNAME="admin" ^
    -e AUTH_PASSWORD="password" ^
    --net=host ^
    ghcr.io/elisevgeniy/gorzdravbot
    ```
   `LOKI_URL=` - можно оставить пустым

### 🐳 С помощью Docker (docker compose)

   1. Скачать [docker-compose.yaml](docker/docker-compose.yml) и [.env_example](docker/.env_example)
   2. Заполнить `.env_example` и переименовать в `.env`.
   3. Запустить командой `docker compose up -d` 

### ♨️ С помощью JAR файла

1. Скачать `jar` файл из [релиза](https://github.com/elisevgeniy/GorzdravBot/releases)
2. Получить `username`, `password` и `database name` от запущенной PostgreSQL базы данных
3. Запустить приложение с помощью cmd на windows (для запуска с помощью bash на linux замените `^` на `\`)

   ``` 
   java ^
   -DDATABASE_URL=localhost:5432 ^
   -DDATABASE_USER=user ^
   -DDATABASE_PASSWORD=123 ^
   -DDATABASE_NAME=gorzdrav_bot ^
   -DBOT_NAME=NameOfYourBot ^
   -DBOT_TOKEN=12334566:SDfsdfdsfdsfsdfsdFDSfdsfdsfsdf ^
   -DLOKI_URL=http://localhost:3100 ^
   -DLOKI_AUTH=username ^
   -DLOKI_PASSWORD=password ^
   -DAUTH_USERNAME=admin ^
   -DAUTH_PASSWORD=password ^
   -jar GorzdravBot-{version}.jar
   ```
   `LOKI_URL=` - можно оставить пустым

## 📃 Как работает бот

1. Пользователь заполняет данные пациента и создаёт задачу на поиск номерков.
2. Бот с определённой периодичностью запускает поиск номерков по незавершённым задачам.
3. Если номерки есть, они направляются пользователю в виде кнопок. При нажатии на кнопку происходит запись на выбранное
   время.
4. Если пользователь не успел нажать кнопку, то через заданное время бот сам записывает **на самое ранее время**.

При создании задачи существует возможность создать ограничения для поиска:
1. По времени можно задать диапазоны, в которые номерок должен попадать и/или не попадать;
2. По дате можно задать:
    1. конченую дату, дальше, которой номерки не рассматриваются;
    2. диапазоны дат, в которые номерок должен попадать и/или не попадать;