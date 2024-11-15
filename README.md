# GorzdravBot
Телеграм-бот для отслеживания талончиков и автоматической записи к врачам СПб.

В настоящий момент бот проверен исключительно ручным тестированием из-за личной потребностив боте. </br>
Однако бот работает.
<!--Блок информации о репозитории в бейджах-->
![Static Badge](https://img.shields.io/badge/elisevgeniy-GorzdravBot-GorzdravBot)
![GitHub top language](https://img.shields.io/github/languages/top/elisevgeniy/GorzdravBot)
![GitHub](https://img.shields.io/github/license/elisevgeniy/GorzdravBot)
![GitHub issues](https://img.shields.io/github/issues/elisevgeniy/GorzdravBot)
![GitHub Repo stars](https://img.shields.io/github/stars/elisevgeniy/GorzdravBot)

<!--Установка-->
## Установка
В будущем планируется сборка в docker-образ, сейчас доступна установка через Maven
1. Клонирование репозитория
   
```git clone https://github.com/elisevgeniy/GorzdravBot.git```

2. Переход в директорию GorzdravBot

```cd GorzdravBot```

3. Настройка параметров приложения

    1. Зарегистрировать бота, получть `name` и `token` бота. Подробности на [оф. сайте](https://core.telegram.org/bots/features#creating-a-new-bot)
    2. Получить `username`, `password` и `database name` от запущенной PostgreSQL базы данных
    3. Зполнить файл `src/main/resources/application.yaml` вышеуказанными параметрами
    4. Запустить бота ```mvn spring-boot:run```
## TODO
- [ ] https://github.com/elisevgeniy/GorzdravBot/issues/13
- [ ] https://github.com/elisevgeniy/GorzdravBot/issues/14
- [ ] Переработать архитектуру
