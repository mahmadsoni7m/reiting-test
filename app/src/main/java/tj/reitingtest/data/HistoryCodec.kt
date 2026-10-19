package tj.reitingtest.data

import org.json.JSONArray
import org.json.JSONObject
import tj.reitingtest.domain.TestRecord

object HistoryCodec {
    fun encode(list: List<TestRecord>): String {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().put("id", it.id).put("s", it.subjectId).put("t", it.timestamp)
                .put("n", it.total).put("c", it.correct))
        }
        return arr.toString()
    }

    fun decode(raw: String?): List<TestRecord> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { i ->
                val o = arr.optJSONObject(i) ?: return@mapNotNull null
                TestRecord(o.optString("id"), o.optString("s"), o.optLong("t"), o.optInt("n"), o.optInt("c"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
