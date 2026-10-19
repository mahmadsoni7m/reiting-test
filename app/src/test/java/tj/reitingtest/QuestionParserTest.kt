package tj.reitingtest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.reitingtest.data.QuestionParser

class QuestionParserTest {
    private fun q(id: String = "x_001", subject: String = "ecology", correct: String = "B", extra: String = "") =
        """{"id":"$id","subjectId":"$subject","question":"Q?","options":{"A":"1","B":"2","C":"3","D":"4"$extra},
           "correctAnswer":"$correct","explanation":"E"}"""

    @Test fun validQuestionParses() {
        val r = QuestionParser.parse("f.json", "ecology", "[${q()}]")
        assertTrue(r.issues.toString(), r.issues.isEmpty())
        assertEquals(1, r.questions.size)
        assertEquals("B", r.questions[0].correctAnswer)
    }

    @Test fun malformedJsonGivesDiagnosticNotCrash() {
        val r = QuestionParser.parse("f.json", "ecology", "[{")
        assertTrue(r.questions.isEmpty())
        assertEquals(1, r.issues.size)
    }

    @Test fun missingFieldIsReported() {
        val r = QuestionParser.parse("f.json", "ecology", """[{"id":"x","subjectId":"ecology"}]""")
        assertTrue(r.questions.isEmpty())
        assertTrue(r.issues.any { it.message.contains("question") })
    }

    @Test fun duplicateIdIsReported() {
        val r = QuestionParser.parse("f.json", "ecology", "[${q()},${q()}]")
        assertEquals(1, r.questions.size)
        assertTrue(r.issues.any { it.message.contains("такрорӣ") })
    }

    @Test fun invalidCorrectAnswerIsReported() {
        val r = QuestionParser.parse("f.json", "ecology", "[${q(correct = "E")}]")
        assertTrue(r.questions.isEmpty())
        assertTrue(r.issues.any { it.message.contains("correctAnswer") })
    }

    @Test fun wrongSubjectAndExtraOptionAreReported() {
        val r = QuestionParser.parse("f.json", "ecology", "[${q(subject = "law", extra = ",\"E\":\"5\"")}]")
        assertTrue(r.questions.isEmpty())
        assertEquals(2, r.issues.size)
    }

    @Test fun nonArrayRootIsReported() {
        val r = QuestionParser.parse("f.json", "ecology", "{}")
        assertEquals(1, r.issues.size)
    }
}
