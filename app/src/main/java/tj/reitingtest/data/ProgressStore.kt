package tj.reitingtest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tj.reitingtest.domain.AppSettings
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.Progress
import tj.reitingtest.domain.QuizResult
import tj.reitingtest.domain.TestRecord
import tj.reitingtest.domain.ThemeMode

private val Context.reitingDataStore: DataStore<Preferences> by preferencesDataStore(name = "reiting_prefs")

class ProgressStore(context: Context) {
    private val ds: DataStore<Preferences> = context.applicationContext.reitingDataStore

    private object K {
        val theme = stringPreferencesKey("theme")
        val lang = stringPreferencesKey("lang")
        val history = stringPreferencesKey("history")
        val mastered = stringSetPreferencesKey("mastered")
        val wrong = stringSetPreferencesKey("wrong")
        val answered = intPreferencesKey("total_answered")
        val correct = intPreferencesKey("total_correct")
    }

    val settings: Flow<AppSettings> = ds.data.map {
        AppSettings(ThemeMode.fromName(it[K.theme]), Lang.fromCode(it[K.lang]))
    }

    val progress: Flow<Progress> = ds.data.map {
        Progress(
            history = HistoryCodec.decode(it[K.history]),
            masteredIds = it[K.mastered] ?: emptySet(),
            wrongIds = it[K.wrong] ?: emptySet(),
            totalAnswered = it[K.answered] ?: 0,
            totalCorrect = it[K.correct] ?: 0,
        )
    }

    suspend fun setTheme(mode: ThemeMode) { ds.edit { it[K.theme] = mode.name } }
    suspend fun setLang(lang: Lang) { ds.edit { it[K.lang] = lang.code } }

    suspend fun recordAttempt(record: TestRecord, result: QuizResult) {
        ds.edit { p ->
            val history = (HistoryCodec.decode(p[K.history]) + record).takeLast(300)
            p[K.history] = HistoryCodec.encode(history)
            p[K.mastered] = (p[K.mastered] ?: emptySet()) + result.correctIds
            p[K.wrong] = ((p[K.wrong] ?: emptySet()) - result.correctIds) + result.wrongIds
            p[K.answered] = (p[K.answered] ?: 0) + result.correct + result.wrong
            p[K.correct] = (p[K.correct] ?: 0) + result.correct
        }
    }

    suspend fun resetProgress() {
        ds.edit {
            it.remove(K.history); it.remove(K.mastered); it.remove(K.wrong)
            it.remove(K.answered); it.remove(K.correct)
        }
    }
}
