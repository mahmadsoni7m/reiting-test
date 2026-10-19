package tj.reitingtest

import tj.reitingtest.data.Catalog
import tj.reitingtest.data.QuestionRepository
import tj.reitingtest.data.QuestionSource
import tj.reitingtest.domain.OPTION_KEYS
import tj.reitingtest.domain.Question
import java.io.File

object TestSupport {
    private val dir = File("src/main/assets/questions")

    fun catalog(): Catalog = QuestionRepository(QuestionSource { name ->
        File(dir, name).readText(Charsets.UTF_8)
    }).loadBlocking()

    fun sampleQuestions(n: Int, subject: String = "s"): List<Question> = (1..n).map { i ->
        Question("q$i", subject, "Савол $i",
            mapOf("A" to "a$i", "B" to "b$i", "C" to "c$i", "D" to "d$i"),
            OPTION_KEYS[i % 4], "Шарҳ $i")
    }
}
