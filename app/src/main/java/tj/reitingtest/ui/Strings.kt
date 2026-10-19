package tj.reitingtest.ui

import androidx.compose.runtime.compositionLocalOf
import tj.reitingtest.domain.Lang

class T(private val lang: Lang) {
    private fun p(tg: String, ru: String, en: String): String = when (lang) {
        Lang.TG -> tg
        Lang.RU -> ru
        Lang.EN -> en
    }

    val appName = "reiting-test"
    val navHome get() = p("Асосӣ", "Главная", "Home")
    val navSubjects get() = p("Фанҳо", "Предметы", "Subjects")
    val navResults get() = p("Натиҷаҳо", "Результаты", "Results")
    val navSettings get() = p("Танзимот", "Настройки", "Settings")

    val homeTitle get() = p("Салом! Ба рейтинг омода шавед", "Привет! Готовимся к рейтингу", "Hi! Get ready for your rating test")
    val homeSubtitle get() = p("Фанро интихоб кунед ва машқро оғоз намоед", "Выберите предмет и начните практику", "Pick a subject and start practising")
    val statAnswered get() = p("Ҷавобҳо", "Ответы", "Answers")
    val statTests get() = p("Тестҳо", "Тесты", "Tests")
    val statAccuracy get() = p("Дурустӣ", "Точность", "Accuracy")
    val subjectsHeader get() = p("Фанҳо", "Предметы", "Subjects")
    val practiceMistakes get() = p("Такрори хатоҳо", "Работа над ошибками", "Practise mistakes")
    fun mistakesSaved(n: Int) = p("Хатоҳои захирашуда: $n", "Сохранённых ошибок: $n", "Saved mistakes: $n")
    val noSavedMistakes get() = p("Хатои захирашуда нест", "Нет сохранённых ошибок", "No saved mistakes")

    fun questionsCount(n: Int) = p("$n савол", "Вопросов: $n", "$n questions")
    fun learned(done: Int, total: Int) = p("Азхуд шуд: $done/$total", "Освоено: $done/$total", "Mastered: $done/$total")
    fun bestScore(pct: Int) = p("Беҳтарин: $pct%", "Лучший: $pct%", "Best: $pct%")
    val notTried get() = p("Ҳанӯз супорида нашудааст", "Ещё не проходили", "Not attempted yet")
    val unavailable get() = p("Файл бор нашуд", "Файл не загружен", "File failed to load")

    val chooseTest get() = p("Шумораи саволҳоро интихоб кунед", "Выберите количество вопросов", "Choose the number of questions")
    fun nQuestions(n: Int) = p("$n савол", "$n вопросов", "$n questions")
    fun allQuestions(n: Int) = p("Ҳамаи саволҳо ($n)", "Все вопросы ($n)", "All questions ($n)")
    val start get() = p("Оғоз", "Начать", "Start")
    val back get() = p("Бозгашт", "Назад", "Back")

    val next get() = p("Саволи навбатӣ", "Следующий вопрос", "Next question")
    val finish get() = p("Анҷом додан", "Завершить", "Finish")
    val correct get() = p("Дуруст!", "Верно!", "Correct!")
    val wrong get() = p("Нодуруст", "Неверно", "Incorrect")
    val explanation get() = p("Шарҳ", "Пояснение", "Explanation")
    val exitTitle get() = p("Аз тест мебароед?", "Выйти из теста?", "Leave the test?")
    val exitText get() = p("Натиҷаи ин кӯшиш захира намешавад.", "Результат этой попытки не будет сохранён.", "This attempt will not be saved.")
    val exit get() = p("Баромадан", "Выйти", "Leave")
    val stay get() = p("Идома додан", "Продолжить", "Continue")
    fun answerLabel(letter: String, text: String) = p("Ҷавоб $letter: $text", "Ответ $letter: $text", "Answer $letter: $text")

    val resultTitle get() = p("Натиҷаи тест", "Результат теста", "Test result")
    val correctWord get() = p("Дуруст", "Верно", "Correct")
    val wrongWord get() = p("Нодуруст", "Неверно", "Incorrect")
    fun scoreLine(c: Int, t: Int) = p("$c аз $t", "$c из $t", "$c of $t")
    val reviewMistakes get() = p("Дида баромадани хатоҳо", "Просмотр ошибок", "Review mistakes")
    val retake get() = p("Аз нав супоридан", "Пройти заново", "Retake")
    val toHome get() = p("Ба саҳифаи асосӣ", "На главную", "Back to home")
    val perfect get() = p("Аъло! Ҳамаи ҷавобҳо дуруст аст.", "Отлично! Все ответы верны.", "Excellent! All answers are correct.")

    val reviewTitle get() = p("Хатоҳо", "Ошибки", "Mistakes")
    val yourAnswer get() = p("Ҷавоби шумо", "Ваш ответ", "Your answer")
    val rightAnswer get() = p("Ҷавоби дуруст", "Правильный ответ", "Correct answer")
    val noMistakes get() = p("Хато нест. Баракалло!", "Ошибок нет. Молодец!", "No mistakes. Well done!")

    val resultsTitle get() = p("Натиҷаҳо", "Результаты", "Results")
    val bestBySubject get() = p("Беҳтарин натиҷа аз рӯи фан", "Лучший результат по предметам", "Best score by subject")
    val historyTitle get() = p("Таърихи тестҳо", "История тестов", "Test history")
    val historyEmpty get() = p("Ҳанӯз тест супорида нашудааст.", "Вы ещё не проходили тесты.", "No tests completed yet.")
    val mistakesMode get() = p("Хатоҳо", "Ошибки", "Mistakes")

    val settingsTitle get() = p("Танзимот", "Настройки", "Settings")
    val theme get() = p("Мавзӯъ", "Тема", "Theme")
    val themeSystem get() = p("Мувофиқи система", "Как в системе", "System")
    val themeDark get() = p("Торик", "Тёмная", "Dark")
    val themeLight get() = p("Равшан", "Светлая", "Light")
    val language get() = p("Забони интерфейс", "Язык интерфейса", "Interface language")
    val dataHeader get() = p("Маълумот", "Данные", "Data")
    val resetProgress get() = p("Тоза кардани пешрафт", "Сбросить прогресс", "Reset progress")
    val resetTitle get() = p("Пешрафтро тоза мекунед?", "Сбросить прогресс?", "Reset progress?")
    val resetText get() = p("Таърихи тестҳо ва хатоҳо нест карда мешаванд.", "История тестов и ошибки будут удалены.", "Test history and mistakes will be deleted.")
    val cancel get() = p("Бекор кардан", "Отмена", "Cancel")
    val confirm get() = p("Тасдиқ", "Подтвердить", "Confirm")
    val about get() = p("Дар бораи барнома", "О приложении", "About")
    val aboutText get() = p(
        "Барномаи офлайн барои омодагӣ ба тести рейтингии донишҷӯёни курси аввал.",
        "Офлайн-приложение для подготовки к рейтинговым тестам студентов первого курса.",
        "Offline app for first-year students preparing for rating tests.")
    fun engine(s: String) = p("Муҳаррик: $s", "Движок: $s", "Engine: $s")
    val diagnostics get() = p("Санҷиши файлҳои савол", "Проверка файлов вопросов", "Question files check")
    fun diagOk(n: Int) = p("Ҳамаи файлҳо дурустанд ($n савол)", "Все файлы в порядке ($n вопросов)", "All files are valid ($n questions)")
    fun diagIssues(n: Int) = p("Мушкилот ёфт шуд: $n", "Найдено проблем: $n", "Issues found: $n")

    val loading get() = p("Саволҳо боргирӣ мешаванд…", "Загрузка вопросов…", "Loading questions…")
    val errorTitle get() = p("Саволҳоро бор кардан муяссар нашуд", "Не удалось загрузить вопросы", "Could not load questions")
    val retry get() = p("Аз нав кӯшиш кардан", "Повторить", "Retry")
    val emptySubject get() = p("Барои ин фан савол нест", "Для этого предмета нет вопросов", "No questions for this subject")
}

val LocalT = compositionLocalOf { T(Lang.TG) }
