package tj.reitingtest

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.reitingtest.data.AssetQuestionSource
import tj.reitingtest.data.QuestionRepository

class AssetsInstrumentedTest {
    @Test fun packagedAssetsLoadCleanly() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("tj.reitingtest", ctx.packageName)
        val catalog = QuestionRepository(AssetQuestionSource(ctx)).loadBlocking()
        assertTrue(catalog.issues.joinToString("\n"), catalog.issues.isEmpty())
        assertTrue(catalog.loadErrors.toString(), catalog.loadErrors.isEmpty())
        assertEquals(400, catalog.all.size)
    }
}
