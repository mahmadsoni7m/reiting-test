package tj.reitingtest.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import tj.reitingtest.domain.OPTION_KEYS
import tj.reitingtest.domain.Question

data class ValidationIssue(val file: String, val location: String, val message: String) {
    override fun toString(): String = "$file [$location]: $message"
}

data class ParseResult(val questions: List<Question>, val issues: List<ValidationIssue>)

object QuestionParser {
    /** Файли JSON-ро месанҷад. Саволҳои хато гузаронида шуда, ҳар хато ҳамчун ташхис бармегардад. */
    fun parse(fileName: String, expectedSubjectId: String, json: String): ParseResult {
        val issues = mutableListOf<ValidationIssue>()
        val array = try {
            JSONArray(json)
        } catch (e: JSONException) {
            return ParseResult(emptyList(), listOf(ValidationIssue(fileName, "реша", "JSON-и вайрон: ${e.message}")))
        }
        val seen = HashSet<String>()
        val out = ArrayList<Question>()
        for (i in 0 until array.length()) {
            val loc = "#${i + 1}"
            val obj = array.optJSONObject(i)
            if (obj == null) {
                issues += ValidationIssue(fileName, loc, "элемент объект нест")
                continue
            }
            val before = issues.size
            val id = str(obj, "id")
            val subjectId = str(obj, "subjectId")
            val text = str(obj, "question")
            val explanation = str(obj, "explanation")
            val correct = str(obj, "correctAnswer")
            for ((name, v) in listOf("id" to id, "subjectId" to subjectId, "question" to text,
                "explanation" to explanation, "correctAnswer" to correct)) {
                if (v == null) issues += ValidationIssue(fileName, loc, "майдони '$name' нест ё холӣ аст")
            }
            val optObj = obj.optJSONObject("options")
            val options = LinkedHashMap<String, String>()
            if (optObj == null) {
                issues += ValidationIssue(fileName, loc, "майдони 'options' нест")
            } else {
                for (k in OPTION_KEYS) {
                    val v = str(optObj, k)
                    if (v == null) issues += ValidationIssue(fileName, loc, "варианти '$k' нест ё холӣ аст")
                    else options[k] = v
                }
                val keys = optObj.keys().asSequence().toList()
                val extra = keys.filter { it !in OPTION_KEYS }
                if (extra.isNotEmpty()) issues += ValidationIssue(fileName, loc, "калидҳои иловагӣ дар options: $extra")
                if (options.size == 4 && options.values.toSet().size != 4) {
                    issues += ValidationIssue(fileName, loc, "вариантҳо бояд гуногун бошанд")
                }
            }
            if (correct != null && correct !in OPTION_KEYS) {
                issues += ValidationIssue(fileName, loc, "correctAnswer бояд яке аз A/B/C/D бошад, '$correct' ёфт шуд")
            }
            if (subjectId != null && subjectId != expectedSubjectId) {
                issues += ValidationIssue(fileName, loc, "subjectId бояд '$expectedSubjectId' бошад, '$subjectId' ёфт шуд")
            }
            if (id != null && !seen.add(id)) {
                issues += ValidationIssue(fileName, loc, "ID-и такрорӣ: '$id'")
            }
            if (issues.size == before) {
                out += Question(id!!, subjectId!!, text!!, options, correct!!, explanation!!)
            }
        }
        return ParseResult(out, issues)
    }

    private fun str(obj: JSONObject, key: String): String? =
        if (obj.has(key) && !obj.isNull(key)) obj.optString(key).trim().ifEmpty { null } else null
}
