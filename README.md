# DailyQuiz — Android-приложение для ежедневных викторин

**DailyQuiz** — нативное Android-приложение на Kotlin и Jetpack Compose, которое загружает вопросы викторины из Open Trivia DB API, даёт пользователю пройти тест из 5 вопросов и сохраняет историю попыток локально. Проект демонстрирует Clean Architecture + MVVM с Hilt DI, Room, Retrofit и Navigation Compose.

---

## 🛠 Технологический стек

| Категория | Инструмент |
|-----------|-----------|
| **Язык** | Kotlin 2.2, JVM 17 |
| **UI** | Jetpack Compose + Material Design 3 |
| **Навигация** | Navigation Compose 2.9 |
| **DI** | Dagger Hilt 2.57 |
| **Сеть** | Retrofit 3.0 + OkHttp 5 + Gson |
| **БД** | Room 2.7 (SQLite) |
| **Асинхронность** | Kotlin Coroutines + StateFlow + Flow |
| **Парсинг** | Jsoup 1.21 (очистка HTML-entities) |
| **Сборка** | Gradle KTS, AGP 8.11, Version Catalog (libs.versions.toml) |
| **Линтер** | ktlint (плагин org.jlleitschuh.gradle.ktlint) |
| **CI** | GitHub Actions (build + lint + test) |
| **minSdk / targetSdk / compileSdk** | API 26 / 35 / 35 |

---

## 🚀 Ключевой функционал

- **Загрузка вопросов из API** — 5 вопросов с 4 вариантами ответов через Open Trivia DB, обработка сетевых ошибок
- **Экран викторины** — старт → загрузка → 5 вопросов последовательно → результат с персонализированным сообщением
- **Мгновенная обратная связь** — подсветка правильного/неправильного ответа перед переходом к следующему вопросу
- **Сохранение истории** — каждая попытка пишется в Room (таблицы `quiz_attempts` + `questions`)
- **Экран истории** — список попыток (дата, время, звёзды), удаление по долгому нажатию
- **Экран разбора** — детальный просмотр ответов (зелёный ✅ = правильно, красный ❌ = ошибка пользователя)
- **Тёмная тема** — автоматическое переключение по системной настройке, Dynamic Colors на Android 12+
- **Очистка HTML** — экранированные символы (`&quot;`, `&#039;`) корректно отображаются через Jsoup

---

## 📁 Архитектура

```
app/src/main/java/com/example/dailyquiz/
├── QuizApplication.kt          # @HiltAndroidApp
├── di/
│   └── AppModule.kt            # OkHttp, Retrofit, Room, Repository — все провайдеры
│
├── data/                       # 🔷 Data Layer
│   ├── model/
│   │   ├── ApiQuestion.kt      # DTO для API: QuizResponse, ApiQuestion (Gson)
│   │   └── Entities.kt         # Room-сущности + отношение attempt→questions
│   ├── mappers/
│   │   └── Mappers.kt          # ApiQuestion → domain.Question, Entity ↔ Domain
│   ├── repository/
│   │   └── QuizRepositoryImpl.kt
│   └── source/
│       ├── local/              # QuizDatabase, QuizDao, Converters
│       └── remote/             # ApiService (Retrofit-интерфейс)
│
├── domain/                     # 🔶 Domain Layer (чистый Kotlin, без Android)
│   ├── model/
│   │   ├── Question.kt         # Вопрос: текст, ответы, результат
│   │   └── QuizAttempt.kt      # Попытка: id, timestamp, score, questions
│   ├── repository/
│   │   └── QuizRepository.kt   # Интерфейс репозитория
│   └── use_case/               # 5 use cases: GetNew, Save, GetHistory,
│       └── ...                 #   GetAttemptDetails, DeleteAttempt
│
├── ui/                         # 🎨 Presentation Layer
│   ├── MainActivity.kt         # Single Activity, @AndroidEntryPoint
│   ├── theme/                  # Material 3: colors, typography, theme
│   ├── navigation/
│   │   ├── Screen.kt           # Sealed class: Quiz, History, Details
│   │   └── AppNavigation.kt    # NavHost (3 composable-маршрута)
│   └── screens/
│       ├── quiz/               # QuizScreen + QuizViewModel (6 состояний UI)
│       ├── history/            # HistoryScreen + HistoryViewModel
│       └── details/            # DetailsScreen + DetailsViewModel
│
└── util/
    └── Resource.kt             # Sealed class: Success<T> / Error<T>
```

### Схема БД

```
┌──────────────────────┐       ┌──────────────────────────────┐
│    quiz_attempts     │       │          questions           │
├──────────────────────┤       ├──────────────────────────────┤
│ id : INTEGER (PK)   │──┐    │ id : INTEGER (PK)            │
│ timestamp : INTEGER │  │    │ attemptId : INTEGER (FK)     │
│ score : INTEGER     │  └───→│ questionText : TEXT           │
└──────────────────────┘       │ allAnswers : TEXT (CSV)      │
                               │ correctAnswer : TEXT         │
                               │ userAnswer : TEXT            │
                               │ isCorrect : INTEGER (0/1)    │
                               └──────────────────────────────┘
```

---

## 💻 Локальное развертывание

### Требования

- **Android Studio** Iguana (2023.2+) или новее
- **JDK 17+** (рекомендуется 17 или 21)
- **Android SDK** API 35
- Физическое устройство или эмулятор с API 26+

### Быстрый старт

```bash
# 1. Клонировать
git clone https://github.com/<your-org>/DailyQuiz.git
cd DailyQuiz

# 2. Открыть в Android Studio
#    File → Open → DailyQuiz

# 3. Дождаться синхронизации Gradle
#    Все зависимости подтянутся автоматически из version catalog

# 4. Запустить
#    Выбрать target device → Run (▶)
```

### Gradle-команды

```bash
./gradlew assembleDebug           # debug APK
./gradlew assembleRelease         # release APK (с ProGuard/R8)
./gradlew lint                    # статический анализ (Android Lint)
./gradlew ktlintCheck             # проверка форматирования Kotlin
./gradlew test                    # unit-тесты
./gradlew connectedAndroidTest    # instrumented-тесты (требуется устройство)
```

### Настройка

Приложение использует публичный API [Open Trivia DB](https://opentdb.com/) — **ключи не требуются**. Просто соберите и запустите.

Базовый URL логирования HTTP настраиваются через `BuildConfig`:
- `BuildConfig.BASE_URL` — endpoint API
- `BuildConfig.LOG_HTTP` — флаг body-логирования OkHttp (`true` в debug, `false` в release)

---

## 🔌 API

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

> Swagger/OpenAPI-спецификация не предусмотрена — собственного бэкенда нет.

---

## 🧪 Тестирование

Unit-тесты (`test`):
- `MappersTest` — маппинг API-ответов с HTML-entities, корректность перемешивания ответов
- `ConvertersTest` — сериализация/десериализация List<String> через Room TypeConverter
- `QuizViewModelTest` — проверка всех состояний (START → LOADING → IN_PROGRESS → RESULTS/ERROR), подсчёт баллов, сброс
- `ResourceTest` — корректность sealed class Resource

```bash
./gradlew test                     # все unit-тесты
./gradlew test --tests *.Mappers*  # конкретный класс
```

---

## 📄 Code & Structure Review (проделанные улучшения)

- ✅ Исправлен `.gitignore` — добавлены `.kotlin/`, `.idea/`, `*.hprof`, `*.log`, `*.apk`, `*.aab`
- ✅ Удалены из git-трекинга `.idea/` и `.kotlin/errors/`
- ✅ Все строки UI вынесены в `strings.xml` (ранее были хардкодом в Compose)
- ✅ Настроен ProGuard/R8 — `keep` для Hilt, Retrofit, Room, Gson, моделей
- ✅ Выключено body-логирование OkHttp в release (через `BuildConfig.LOG_HTTP`)
- ✅ BASE_URL вынесен в `buildConfigField`
- ✅ Добавлен `fallbackToDestructiveMigration()` для Room
- ✅ Переименован `ui/Theme/` → `ui/theme/` (соблюдение Kotlin naming convention)
- ✅ Доменные модели Question сделаны иммутабельными (`val` вместо `var`)
- ✅ Gson в Room TypeConverters заменён на `joinToString`/`split`
- ✅ Добавлен `.editorconfig` (стиль кода, charset, end-of-line)
- ✅ Добавлен GitHub Actions CI (сборка + ktlint + тесты)
- ✅ Добавлен ktlint (плагин org.jlleitschuh.gradle.ktlint)
- ✅ Написаны unit-тесты (Mappers, Converters, QuizViewModel, Resource)

---

## 🤝 Вклад

Pull Request’ы приветствуются. Стандартный GitHub Flow:

1. Fork
2. `git checkout -b feature/your-feature`
3. Внести изменения
4. `./gradlew ktlintCheck test` — проверить стиль и тесты
5. Открыть PR в `master`
