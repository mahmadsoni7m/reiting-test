package tj.reitingtest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.reitingtest.domain.KotlinShuffle
import tj.reitingtest.domain.MISTAKES_ID
import tj.reitingtest.domain.OPTION_KEYS
import tj.reitingtest.domain.QuizBuilder

class QuizEngineTest {
    private val pool = TestSupport.sampleQuestions(30)

    private fun session(count: Int, seed: Long = 1L) =
        QuizBuilder.build(pool, count, "s", seed, 0L)!!

    @Test fun countIsRespectedAndCappedByPool() {
        assertEquals(10, session(10).total)
        assertEquals(30, session(50).total)
        assertEquals(30, session(0).total)
    }

    @Test fun emptyPoolGivesNoSession() {
        assertEquals(null, QuizBuilder.build(emptyList(), 10, "s", 1L, 0L))
    }

    @Test fun questionsInSessionAreUnique() {
        val ids = session(20).items.map { it.question.id }
        assertEquals(20, ids.toSet().size)
    }

    @Test fun shuffledOptionsKeepCorrectAnswer() {
        for (seed in 1L..200L) {
            session(30, seed).items.forEach { p ->
                val correctText = p.question.options.getValue(p.question.correctAnswer)
                val shownKey = p.optionOrder.first { p.isCorrect(it) }
                assertEquals(correctText, p.textOf(shownKey))
                assertEquals(OPTION_KEYS, p.optionOrder.sorted())
            }
        }
    }

    @Test fun differentSeedsGiveDifferentOrders() {
        val a = session(30, 1L).items.map { it.question.id }
        val b = session(30, 2L).items.map { it.question.id }
        assertFalse(a == b)
    }

    @Test fun questionCanBeAnsweredOnlyOnce() {
        val s = session(10)
        val key = s.current.optionOrder[0]
        val after = s.select(key)
        val again = after.select(s.current.optionOrder[1])
        assertSame(after, again)
        assertEquals(key, again.currentAnswer)
    }

    @Test fun cannotAdvanceWithoutAnswering() {
        val s = session(10)
        assertSame(s, s.next())
    }

    @Test fun scoringCountsCorrectAndWrong() {
        var s = session(10)
        for (i in 0 until 10) {
            val correctKey = s.current.question.correctAnswer
            val wrongKey = OPTION_KEYS.first { it != correctKey }
            s = s.select(if (i < 7) correctKey else wrongKey)
            if (!s.isLastQuestion) s = s.next()
        }
        assertTrue(s.isReadyToFinish)
        val r = s.result()
        assertEquals(10, r.total)
        assertEquals(7, r.correct)
        assertEquals(3, r.wrong)
        assertEquals(70, r.percent)
        assertEquals(3, r.wrongItems.size)
        assertEquals(7, r.correctIds.size)
    }

    @Test fun invalidKeyIsIgnored() {
        val s = session(10)
        assertSame(s, s.select("Z"))
    }

    @Test fun mistakesSessionKeepsSubjectId() {
        val s = QuizBuilder.build(pool.take(5), 0, MISTAKES_ID, 3L, 0L)!!
        assertEquals(MISTAKES_ID, s.subjectId)
    }

    @Test fun permutationIsValidAndDeterministic() {
        for (n in listOf(1, 2, 4, 50, 400)) {
            val p = KotlinShuffle.permutation(n, 12345L)
            assertEquals((0 until n).toList(), p.sorted())
            assertEquals(p.toList(), KotlinShuffle.permutation(n, 12345L).toList())
        }
        assertEquals(0, KotlinShuffle.permutation(0, 1L).size)
    }
}
