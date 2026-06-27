# DailyQuiz — Android-приложение для ежедневных викторин

Нативное Android-приложение на Kotlin и Jetpack Compose, которое загружает вопросы викторины из Open Trivia DB API, даёт пользователю пройти тест из 5 вопросов и сохраняет историю попыток локально. Проект демонстрирует Clean Architecture + MVVM с Hilt DI, Room, Retrofit и Navigation Compose.

---

## 🛠 Технологический стек

При проектировании архитектуры приложения упор делался на модульность и скорость обработки запросов.

*   **Язык разработки:** Kotlin 2.2, JVM 17
*   **Фреймворки:** Jetpack Compose + Material Design 3
*   **Базы данных:** Room 2.7 (SQLite)
*   **Кэширование и очереди:** Kotlin Coroutines + StateFlow + Flow
*   **Контейнеризация и DevOps:** Dagger Hilt 2.57, Gradle KTS, AGP 8.11
*   **Инструменты тестирования:** JUnit, ktlint

---

## 🚀 Ключевой функционал

*   **Управление пользователями:** Старт викторины → загрузка → 5 вопросов → результат с персонализированным сообщением
*   **Автоматизация логики:** Мгновенная обратная связь — подсветка правильного/неправильного ответа, сохранение истории в Room
*   **Интеграции:** Загрузка вопросов из Open Trivia DB API, очистка HTML-entities через Jsoup
*   **Валидация и безопасность:** Обработка сетевых ошибок, тёмная тема (автоматическое переключение по системной настройке)

---

## 📁 Архитектура и структура проекта

В проекте используется Clean Architecture + MVVM. Это обеспечивает независимость бизнес-логики от внешних библиотек и баз данных.

```text
app/src/main/java/com/example/dailyquiz/
├── QuizApplication.kt          # @HiltAndroidApp
├── di/
│   └── AppModule.kt            # OkHttp, Retrofit, Room, Repository
├── data/                       # 🔷 Data Layer
│   ├── model/                  # DTO: QuizResponse, ApiQuestion (Gson), Room-сущности
│   ├── mappers/                # ApiQuestion → domain.Question, Entity ↔ Domain
│   ├── repository/             # QuizRepositoryImpl
│   └── source/
│       ├── local/              # QuizDatabase, QuizDao, Converters
│       └── remote/             # ApiService (Retrofit-интерфейс)
├── domain/                     # 🔶 Domain Layer (чистый Kotlin)
│   ├── model/                  # Question, QuizAttempt
│   ├── repository/             # QuizRepository (интерфейс)
│   └── use_case/               # 5 use cases
├── ui/                         # 🎨 Presentation Layer
│   ├── MainActivity.kt         # Single Activity
│   ├── theme/                  # Material 3: colors, typography, theme
│   ├── navigation/             # Screen (sealed class), AppNavigation (NavHost)
│   └── screens/
│       ├── quiz/               # QuizScreen + QuizViewModel (6 состояний UI)
│       ├── history/            # HistoryScreen + HistoryViewModel
│       └── details/            # DetailsScreen + DetailsViewModel
└── util/
    └── Resource.kt             # Sealed class: Success<T> / Error<T>
```

---

## 💻 Локальное развертывание

Для запуска проекта в изолированном окружении вам понадобятся **Android Studio** и **JDK 17+**.

### 1. Клонирование репозитория

```bash
git clone https://github.com/<ваш-username>/DailyQuiz.git
cd DailyQuiz
```

### 2. Открыть в Android Studio

File → Open → DailyQuiz. Дождаться синхронизации Gradle.

### 3. Запуск

Выбрать target device → Run (▶). Приложение использует публичный API [Open Trivia DB](https://opentdb.com/) — ключи не требуются.

```bash
# Сборка APK
./gradlew assembleDebug

# Unit-тесты
./gradlew test

# Проверка стиля
./gradlew ktlintCheck
```

---

## 🔌 API и навигация

### Внешний API (Open Trivia DB)

| Метод | Эндпоинт | Описание |
|-------|----------|---------|
| GET | `/api.php?amount=5&type=multiple` | Загрузить 5 вопросов с 4 вариантами |

### Внутренняя навигация (Compose)

| Route | Экран | Вход |
|-------|-------|------|
| `quiz_screen` (start) | Викторина | Стартовый экран приложения |
| `history_screen` | История попыток | Кнопка «История» |
| `details_screen/{attemptId}` | Разбор ответов | Tap по элементу истории |

---

## 👥 Разработчики

* [**Артем Рогачев**](https://github.com/TweetyDMG) — Backend Developer

## 📜 Лицензия

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Проект распространяется на условиях лицензии **MIT**. Полный текст лицензии находится в файле [LICENSE](./LICENSE).
