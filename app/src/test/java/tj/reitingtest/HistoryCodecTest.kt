package tj.reitingtest

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.reitingtest.data.HistoryCodec
import tj.reitingtest.domain.Progress
import tj.reitingtest.domain.TestRecord

class HistoryCodecTest {
    @Test fun roundTrip() {
        val list = listOf(TestRecord("1", "law", 1000L, 20, 15), TestRecord("2", "ecology", 2000L, 10, 4))
        assertEquals(list, HistoryCodec.decode(HistoryCodec.encode(list)))
    }

    @Test fun badInputGivesEmpty() {
        assertEquals(emptyList<TestRecord>(), HistoryCodec.decode("not json"))
        assertEquals(emptyList<TestRecord>(), HistoryCodec.decode(null))
    }

    @Test fun bestPercentPerSubject() {
        val p = Progress(history = listOf(
            TestRecord("1", "law", 1L, 10, 5), TestRecord("2", "law", 2L, 10, 9), TestRecord("3", "ecology", 3L, 10, 2)))
        assertEquals(90, p.bestPercent("law"))
        assertEquals(20, p.bestPercent("ecology"))
        assertEquals(null, p.bestPercent("math"))
    }
}
