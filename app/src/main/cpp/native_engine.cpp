// Муҳаррики хурди бумӣ: омезиши детерминистии Fisher–Yates (xorshift64*).
// Ҳамон алгоритм дар Kotlin (KotlinShuffle) ҳамчун захира мавҷуд аст.
#include <jni.h>
#include <cstdint>
#include <utility>
#include <vector>

namespace {
inline uint64_t nextRandom(uint64_t &s) {
    s ^= s >> 12;
    s ^= s << 25;
    s ^= s >> 27;
    return s * 2685821657736338717ULL;
}
}  // namespace

extern "C" JNIEXPORT jintArray JNICALL
Java_tj_reitingtest_domain_NativeEngine_nativeShuffle(JNIEnv *env, jobject /*thiz*/, jint n, jlong seed) {
    if (n <= 0) return env->NewIntArray(0);
    std::vector<jint> a(static_cast<size_t>(n));
    for (jint i = 0; i < n; ++i) a[static_cast<size_t>(i)] = i;
    uint64_t s = static_cast<uint64_t>(seed) ^ 0x9E3779B97F4A7C15ULL;
    if (s == 0) s = 1;
    for (int k = 0; k < 3; ++k) nextRandom(s);  // разогрев
    for (jint i = n - 1; i > 0; --i) {
        uint64_t r = nextRandom(s);
        jint j = static_cast<jint>(r % static_cast<uint64_t>(i + 1));
        std::swap(a[static_cast<size_t>(i)], a[static_cast<size_t>(j)]);
    }
    jintArray out = env->NewIntArray(n);
    if (out == nullptr) return nullptr;
    env->SetIntArrayRegion(out, 0, n, a.data());
    return out;
}

extern "C" JNIEXPORT jstring JNICALL
Java_tj_reitingtest_domain_NativeEngine_nativeVersion(JNIEnv *env, jobject /*thiz*/) {
    return env->NewStringUTF("C++17 / xorshift64*");
}
