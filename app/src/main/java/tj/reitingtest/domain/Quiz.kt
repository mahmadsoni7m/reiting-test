package tj.reitingtest.domain

/** Савол бо тартиби омехтаи вариантҳо. Дурустӣ аз рӯи калиди аслӣ санҷида мешавад, на аз рӯи ҷой. */
data class PreparedQuestion(val question: Question, val optionOrder: List<String>) {
    init {
        require(optionOrder.sorted() == OPTION_KEYS) { "optionOrder бояд A,B,C,D-ро дошта бошад" }
    }

    fun isCorrect(key: String): Boolean = key == question.correctAnswer
    fun textOf(key: String): String = question.options.getValue(key)
    fun letterOf(key: String): String = OPTION_KEYS[optionOrder.indexOf(key)]
}

data class ReviewItem(val item: PreparedQuestion, val selected: String)

data class QuizResult(
    val total: Int,
    val correct: Int,
    val wrong: Int,
    val percent: Int,
    val correctIds: Set<String>,
    val wrongIds: Set<String>,
    val wrongItems: List<ReviewItem>,
)

data class QuizSession(
    val items: List<PreparedQuestion>,
    val subjectId: String,
    val requestedCount: Int,
    val startedAtMillis: Long,
    val answers: Map<Int, String> = emptyMap(),
    val currentIndex: Int = 0,
) {
    val total: Int get() = items.size
    val current: PreparedQuestion get() = items[currentIndex]
    val currentAnswer: String? get() = answers[currentIndex]
    val isCurrentAnswered: Boolean get() = answers.containsKey(currentIndex)
    val isLastQuestion: Boolean get() = currentIndex == total - 1
    val isReadyToFinish: Boolean get() = isLastQuestion && isCurrentAnswered

    /** Ҷавоб дар як кӯшиш танҳо як маротиба қабул мешавад. */
    fun select(key: String): QuizSession =
        if (isCurrentAnswered || key !in current.optionOrder) this
        else copy(answers = answers + (currentIndex to key))

    fun next(): QuizSession =
        if (!isCurrentAnswered || isLastQuestion) this else copy(currentIndex = currentIndex + 1)

    fun result(): QuizResult {
        val correctIds = mutableSetOf<String>()
        val wrongIds = mutableSetOf<String>()
        val wrongItems = mutableListOf<ReviewItem>()
        items.forEachIndexed { i, p ->
            val sel = answers[i] ?: return@forEachIndexed
            if (p.isCorrect(sel)) correctIds += p.question.id
            else {
                wrongIds += p.question.id
                wrongItems += ReviewItem(p, sel)
            }
        }
        val answered = correctIds.size + wrongIds.size
        val percent = if (total == 0) 0 else correctIds.size * 100 / total
        return QuizResult(total, correctIds.size, total - correctIds.size, percent, correctIds, wrongIds, wrongItems)
            .also { check(answered <= total) }
    }
}

object QuizBuilder {
    /** count <= 0 — ҳамаи саволҳо. */
    fun build(pool: List<Question>, count: Int, subjectId: String, seed: Long, now: Long): QuizSession? {
        if (pool.isEmpty()) return null
        val n = if (count <= 0) pool.size else minOf(count, pool.size)
        val order = NativeEngine.permutation(pool.size, seed)
        val items = order.take(n).mapIndexed { i, idx ->
            val keyOrder = NativeEngine.permutation(OPTION_KEYS.size, seed + (i + 1) * 7919L)
                .map { OPTION_KEYS[it] }
            PreparedQuestion(pool[idx], keyOrder)
        }
        return QuizSession(items, subjectId, if (count <= 0) 0 else count, now)
    }
}
