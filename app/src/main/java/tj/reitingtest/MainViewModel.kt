package tj.reitingtest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tj.reitingtest.data.Catalog
import tj.reitingtest.domain.AppSettings
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.MISTAKES_ID
import tj.reitingtest.domain.Progress
import tj.reitingtest.domain.QuizBuilder
import tj.reitingtest.domain.QuizSession
import tj.reitingtest.domain.TestRecord
import tj.reitingtest.domain.ThemeMode
import java.util.UUID
import kotlin.random.Random

sealed interface CatalogState {
    data object Loading : CatalogState
    data class Ready(val catalog: Catalog) : CatalogState
    data class Error(val message: String) : CatalogState
}

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val reitingApp = app as ReitingApp
    private val store = reitingApp.store
    private val repository = reitingApp.repository

    private val _catalog = MutableStateFlow<CatalogState>(CatalogState.Loading)
    val catalog: StateFlow<CatalogState> = _catalog.asStateFlow()

    val settings: StateFlow<AppSettings> =
        store.settings.stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())
    val progress: StateFlow<Progress> =
        store.progress.stateIn(viewModelScope, SharingStarted.Eagerly, Progress.EMPTY)

    private val _quiz = MutableStateFlow<QuizSession?>(null)
    val quiz: StateFlow<QuizSession?> = _quiz.asStateFlow()
    private var recorded = false

    init { loadCatalog() }

    fun loadCatalog() {
        _catalog.value = CatalogState.Loading
        viewModelScope.launch {
            _catalog.value = try {
                val c = repository.load()
                if (c.hasAnyQuestions) CatalogState.Ready(c) else CatalogState.Error(c.summary())
            } catch (e: Exception) {
                CatalogState.Error(e.message ?: e.toString())
            }
        }
    }

    private fun readyCatalog(): Catalog? = (catalog.value as? CatalogState.Ready)?.catalog

    fun startQuiz(subjectId: String, count: Int): Boolean {
        val c = readyCatalog() ?: return false
        val s = QuizBuilder.build(c.bySubject[subjectId].orEmpty(), count, subjectId,
            Random.nextLong(), System.currentTimeMillis()) ?: return false
        recorded = false
        _quiz.value = s
        return true
    }

    fun startMistakes(): Boolean {
        val c = readyCatalog() ?: return false
        val wrong = progress.value.wrongIds
        val s = QuizBuilder.build(c.all.filter { it.id in wrong }, 0, MISTAKES_ID,
            Random.nextLong(), System.currentTimeMillis()) ?: return false
        recorded = false
        _quiz.value = s
        return true
    }

    fun retake(): Boolean {
        val s = _quiz.value ?: return false
        return if (s.subjectId == MISTAKES_ID) startMistakes() else startQuiz(s.subjectId, s.requestedCount)
    }

    fun select(key: String) { _quiz.update { it?.select(key) } }
    fun next() { _quiz.update { it?.next() } }

    /** Натиҷаро яккарата захира мекунад. */
    fun finish(): Boolean {
        val s = _quiz.value ?: return false
        if (!s.isReadyToFinish) return false
        if (!recorded) {
            recorded = true
            val r = s.result()
            val rec = TestRecord(UUID.randomUUID().toString(), s.subjectId,
                System.currentTimeMillis(), r.total, r.correct)
            viewModelScope.launch { store.recordAttempt(rec, r) }
        }
        return true
    }

    fun abandonQuiz() { _quiz.value = null }

    fun setTheme(mode: ThemeMode) { viewModelScope.launch { store.setTheme(mode) } }
    fun setLang(lang: Lang) { viewModelScope.launch { store.setLang(lang) } }
    fun resetProgress() { viewModelScope.launch { store.resetProgress() } }
}
