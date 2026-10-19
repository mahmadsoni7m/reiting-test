package tj.reitingtest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.reitingtest.CatalogState
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.Progress
import tj.reitingtest.domain.SubjectDef
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.SubjectCard

@Composable
fun SubjectsScreen(
    state: CatalogState,
    progress: Progress,
    lang: Lang,
    onOpenSubject: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val t = LocalT.current
    CatalogGate(state, onRetry) { catalog ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Text(t.navSubjects, style = MaterialTheme.typography.headlineSmall) }
            items(SubjectDef.entries) { s ->
                val list = catalog.bySubject[s.id].orEmpty()
                SubjectCard(
                    subject = s, lang = lang, total = list.size,
                    mastered = masteredIn(catalog, s, progress), best = progress.bestPercent(s.id),
                    available = list.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpenSubject(s.id) },
                )
            }
        }
    }
}
