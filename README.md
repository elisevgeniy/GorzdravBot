# GorzdravBot
Telegram-бот, который поможет вам следить за талонами и автоматически записываться к врачам в Санкт-Петербурге.

<!--Блок информации о репозитории в бейджах-->
![Static Badge](https://img.shields.io/badge/elisevgeniy-GorzdravBot-GorzdravBot)
![GitHub top language](https://img.shields.io/github/languages/top/elisevgeniy/GorzdravBot)
![GitHub](https://img.shields.io/github/license/elisevgeniy/GorzdravBot)
![GitHub issues](https://img.shields.io/github/issues/elisevgeniy/GorzdravBot)
![GitHub Repo stars](https://img.shields.io/github/stars/elisevgeniy/GorzdravBot)

## Как работает бот
1. Пользователь заполняет данные пациента и создаёт задачу на поиск номерков.
2. Бот с определённой периодичностью запускает поиск номерков по незавершённым задачам.
3. Если номерки есть, они направляются пользователю в виде кнопок. При нажатии на кнопку происходит запись на выбранное время.
4. Если пользователь не успел нажать кнопку, то через заданное время бот сам записывает **на самое ранее время**.

При создании задачи существует возможность создать ограничения для поиска:
1. По времени можно задать диапазоны, в которые номерок должен попадать и/или не попадать;
2. По дате можно задать:
   1. конченую дату, дальше, которой номерки не рассматриваются;
   2. диапазоны дат, в которые номерок должен попадать и/или не попадать;

## Регистрация бота
Для работы бота необходимо его зарегистрировать и получть `name` и `token` бота. 

Подробности на [оф. сайте](https://core.telegram.org/bots/features#creating-a-new-bot).

## Запуск
Есть 3 варианта запуска: с помощью [Maven](#с-помощью-maven), [JAR файла](#с-помощью-jar-файла) и [Docker](#с-помощью-docker).

### С помощью Maven
   1. Скопировать репозиторий
      ```
      git clone https://github.com/elisevgeniy/GorzdravBot.git
      cd GorzdravBot
      ```
   2. Получить `username`, `password` и `database name` от запущенной PostgreSQL базы данных
   3. Зполнить файл `src/main/resources/application.yaml` вышеуказанными параметрами
   4. Запустить бота ```mvnw spring-boot:run```

### С помощью Docker
   1. Скопировать репозиторий
       ```
       git clone https://github.com/elisevgeniy/GorzdravBot.git
       cd GorzdravBot
       ```
   2. Скопировать `cp ./build/.env_example ./build/.env` и заполнить `.env`
   3. Собрать приложение с помощью Maven: `mvnw package`
   3. Запустить приложение `cd build && docker compose up --build`

### С помощью JAR файла
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
