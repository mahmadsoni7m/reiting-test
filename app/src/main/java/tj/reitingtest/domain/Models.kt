package tj.reitingtest.domain

val OPTION_KEYS: List<String> = listOf("A", "B", "C", "D")
const val MISTAKES_ID = "mistakes"

enum class Lang(val code: String, val label: String) {
    TG("tg", "Тоҷикӣ"), RU("ru", "Русский"), EN("en", "English");

    companion object {
        fun fromCode(code: String?): Lang = entries.firstOrNull { it.code == code } ?: TG
    }
}

enum class ThemeMode {
    SYSTEM, DARK, LIGHT;

    companion object {
        fun fromName(name: String?): ThemeMode = entries.firstOrNull { it.name == name } ?: DARK
    }
}

data class AppSettings(val theme: ThemeMode = ThemeMode.DARK, val lang: Lang = Lang.TG)

data class Question(
    val id: String,
    val subjectId: String,
    val text: String,
    val options: Map<String, String>,
    val correctAnswer: String,
    val explanation: String,
)

enum class SubjectDef(
    val id: String,
    val fileName: String,
    private val tg: String,
    private val ru: String,
    private val en: String,
) {
    HISTORY("history_tajikistan", "history_tajikistan.json",
        "Таърихи муосири Тоҷикистон", "Новейшая история Таджикистана", "Modern History of Tajikistan"),
    GEOGRAPHY("economic_geography_demography", "economic_geography_demography.json",
        "Географияи иқтисодии Тоҷикистон бо асосҳои демография",
        "Экономическая география Таджикистана с основами демографии",
        "Economic Geography of Tajikistan and Demography"),
    FOREIGN("foreign_language", "foreign_language.json",
        "Забони хориҷӣ", "Иностранный язык", "Foreign Language"),
    ECOLOGY("ecology", "ecology.json", "Экология", "Экология", "Ecology"),
    LAW("law", "law.json", "Ҳуқуқ", "Право", "Law"),
    MATH("higher_mathematics", "higher_mathematics.json",
        "Математикаи олӣ", "Высшая математика", "Higher Mathematics"),
    POLITICS("political_science", "political_science.json",
        "Сиёсатшиносӣ", "Политология", "Political Science"),
    RUSSIAN("russian_language", "russian_language.json",
        "Забони русӣ", "Русский язык", "Russian Language");

    fun title(lang: Lang): String = when (lang) {
        Lang.TG -> tg
        Lang.RU -> ru
        Lang.EN -> en
    }

    companion object {
        fun byId(id: String): SubjectDef? = entries.firstOrNull { it.id == id }
    }
}

data class TestRecord(
    val id: String,
    val subjectId: String,
    val timestamp: Long,
    val total: Int,
    val correct: Int,
) {
    val percent: Int get() = if (total == 0) 0 else correct * 100 / total
}

data class Progress(
    val history: List<TestRecord> = emptyList(),
    val masteredIds: Set<String> = emptySet(),
    val wrongIds: Set<String> = emptySet(),
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
) {
    val accuracyPercent: Int get() = if (totalAnswered == 0) 0 else totalCorrect * 100 / totalAnswered

    fun bestPercent(subjectId: String): Int? =
        history.filter { it.subjectId == subjectId }.maxOfOrNull { it.percent }

    companion object {
        val EMPTY = Progress()
    }
}
