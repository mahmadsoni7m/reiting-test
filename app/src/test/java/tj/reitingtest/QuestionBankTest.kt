package tj.reitingtest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.reitingtest.domain.OPTION_KEYS
import tj.reitingtest.domain.SubjectDef

class QuestionBankTest {
    private val catalog = TestSupport.catalog()

    @Test fun allFilesLoadWithoutIssues() {
        assertTrue("Хатоҳои боркунӣ: ${catalog.loadErrors}", catalog.loadErrors.isEmpty())
        assertTrue("Мушкилоти JSON:\n${catalog.issues.joinToString("\n")}", catalog.issues.isEmpty())
    }

    @Test fun eightSubjectsEachHasAtLeast50Questions() {
        assertEquals(8, SubjectDef.entries.size)
        SubjectDef.entries.forEach { s ->
            val n = catalog.bySubject[s.id].orEmpty().size
            assertTrue("${s.id}: $n < 50", n >= 50)
        }
    }

    @Test fun totalIs400() {
        assertEquals(400, catalog.all.size)
    }

    @Test fun idsAreUniqueAcrossAllFiles() {
        val ids = catalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test fun everyQuestionHasFourDistinctOptionsAndOneValidKey() {
        catalog.all.forEach { q ->
            assertEquals(q.id, OPTION_KEYS, q.options.keys.toList())
            assertEquals(q.id, 4, q.options.values.toSet().size)
            assertTrue(q.id, q.correctAnswer in OPTION_KEYS)
            assertTrue(q.id, q.explanation.isNotBlank() && q.text.isNotBlank())
        }
    }

    @Test fun correctAnswerIsNotAlwaysInTheSamePosition() {
        SubjectDef.entries.forEach { s ->
            val positions = catalog.bySubject[s.id].orEmpty().map { it.correctAnswer }.toSet()
            assertEquals(s.id, 4, positions.size)
        }
    }

    @Test fun questionTextsAreNotRepeated() {
        val texts = catalog.all.map { it.text.trim().lowercase() }
        assertEquals(texts.size, texts.toSet().size)
    }
}
