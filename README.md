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

## Установка
1. Клонирование репозитория
   
```
git clone https://github.com/elisevgeniy/GorzdravBot.git
cd GorzdravBot
```

2. Запуск (либо 3, либо 4 пункты)
3. С помощью Maven
   1. Зарегистрировать бота, получть `name` и `token` бота. Подробности на [оф. сайте](https://core.telegram.org/bots/features#creating-a-new-bot)
   2. Получить `username`, `password` и `database name` от запущенной PostgreSQL базы данных
   3. Зполнить файл `src/main/resources/application.yaml` вышеуказанными параметрами
   4. Запустить бота ```mvnw spring-boot:run```
4. С помощью Docker
   1. Зарегистрировать бота, получть `name` и `token` бота. Подробности на [оф. сайте](https://core.telegram.org/bots/features#creating-a-new-bot) 
   2. Скопировать `cp ./build/.env_example ./build/.env` и заполнить `.env` 
   3. Запустить приложение `cd build && docker compose up --build`          
