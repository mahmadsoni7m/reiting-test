package tj.reitingtest.domain

/** Омезиши детерминистӣ. Агар китобхонаи C++ бор нашавад, нусхаи Kotlin кор мекунад. */
object KotlinShuffle {
    private const val GOLDEN: Long = -7046029254386353131L // 0x9E3779B97F4A7C15
    private const val MULT: Long = 2685821657736338717L

    private class XorShift(var s: Long) {
        fun next(): Long {
            s = s xor (s ushr 12)
            s = s xor (s shl 25)
            s = s xor (s ushr 27)
            return s * MULT
        }
    }

    fun permutation(n: Int, seed: Long): IntArray {
        if (n <= 0) return IntArray(0)
        val a = IntArray(n) { it }
        var start = seed xor GOLDEN
        if (start == 0L) start = 1L
        val rng = XorShift(start)
        repeat(3) { rng.next() }
        for (i in n - 1 downTo 1) {
            val r = rng.next()
            val j = java.lang.Long.remainderUnsigned(r, (i + 1).toLong()).toInt()
            val t = a[i]; a[i] = a[j]; a[j] = t
        }
        return a
    }
}

object NativeEngine {
    private val loaded: Boolean = try {
        System.loadLibrary("reiting_native")
        true
    } catch (e: Throwable) {
        false
    }

    val isNativeAvailable: Boolean get() = loaded

    fun permutation(n: Int, seed: Long): IntArray {
        if (loaded) {
            try {
                return nativeShuffle(n, seed)
            } catch (e: Throwable) {
                // захира: Kotlin
            }
        }
        return KotlinShuffle.permutation(n, seed)
    }

    fun version(): String {
        if (loaded) {
            try {
                return nativeVersion()
            } catch (e: Throwable) {
                // захира
            }
        }
        return "Kotlin"
    }

    private external fun nativeShuffle(n: Int, seed: Long): IntArray
    private external fun nativeVersion(): String
}
