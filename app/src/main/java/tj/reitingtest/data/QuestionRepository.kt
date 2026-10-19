package tj.reitingtest.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tj.reitingtest.domain.Question
import tj.reitingtest.domain.SubjectDef

fun interface QuestionSource {
    fun read(fileName: String): String
}

class AssetQuestionSource(private val context: Context) : QuestionSource {
    override fun read(fileName: String): String =
        context.assets.open("questions/$fileName").bufferedReader(Charsets.UTF_8).use { it.readText() }
}

data class Catalog(
    val bySubject: Map<String, List<Question>>,
    val issues: List<ValidationIssue>,
    val loadErrors: Map<String, String>,
) {
    val all: List<Question> get() = bySubject.values.flatten()
    val hasAnyQuestions: Boolean get() = bySubject.values.any { it.isNotEmpty() }

    fun summary(): String {
        val lines = loadErrors.map { (k, v) -> "$k: $v" } + issues.take(5).map { it.toString() }
        return lines.joinToString("\n").ifEmpty { "Саволе ёфт нашуд" }
    }
}

class QuestionRepository(private val source: QuestionSource) {
    suspend fun load(): Catalog = withContext(Dispatchers.IO) { loadBlocking() }

    fun loadBlocking(): Catalog {
        val map = LinkedHashMap<String, List<Question>>()
        val issues = mutableListOf<ValidationIssue>()
        val errors = LinkedHashMap<String, String>()
        val globalIds = HashSet<String>()
        for (s in SubjectDef.entries) {
            val text = try {
                source.read(s.fileName)
            } catch (e: Exception) {
                errors[s.id] = "файл хонда нашуд (${s.fileName}): ${e.message}"
                map[s.id] = emptyList()
                continue
            }
            val r = QuestionParser.parse(s.fileName, s.id, text)
            issues += r.issues
            val unique = r.questions.filter { q ->
                val ok = globalIds.add(q.id)
                if (!ok) issues += ValidationIssue(s.fileName, q.id, "ID дар файлҳои дигар низ ҳаст")
                ok
            }
            map[s.id] = unique
        }
        return Catalog(map, issues, errors)
    }
}
