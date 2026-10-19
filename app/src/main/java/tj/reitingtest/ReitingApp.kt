package tj.reitingtest

import android.app.Application
import tj.reitingtest.data.AssetQuestionSource
import tj.reitingtest.data.ProgressStore
import tj.reitingtest.data.QuestionRepository

class ReitingApp : Application() {
    val store: ProgressStore by lazy { ProgressStore(this) }
    val repository: QuestionRepository by lazy { QuestionRepository(AssetQuestionSource(this)) }
}
