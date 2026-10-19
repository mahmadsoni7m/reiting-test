package tj.reitingtest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.reitingtest.CatalogState
import tj.reitingtest.data.Catalog
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.Progress
import tj.reitingtest.domain.SubjectDef
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.StatTile
import tj.reitingtest.ui.components.SubjectCard

fun masteredIn(catalog: Catalog, subject: SubjectDef, progress: Progress): Int =
    catalog.bySubject[subject.id].orEmpty().count { it.id in progress.masteredIds }

@Composable
fun HomeScreen(
    state: CatalogState,
    progress: Progress,
    lang: Lang,
    onOpenSubject: (String) -> Unit,
    onPracticeMistakes: () -> Unit,
    onRetry: () -> Unit,
) {
    val t = LocalT.current
    CatalogGate(state, onRetry) { catalog ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(t.homeTitle, style = MaterialTheme.typography.headlineSmall)
                Text(t.homeSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp))
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(t.statAnswered, "${progress.totalAnswered}", Modifier.weight(1f))
                    StatTile(t.statTests, "${progress.history.size}", Modifier.weight(1f))
                    StatTile(t.statAccuracy, "${progress.accuracyPercent}%", Modifier.weight(1f))
                }
            }
            item {
                val n = progress.wrongIds.size
                OutlinedButton(
                    onClick = onPracticeMistakes,
                    enabled = n > 0,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Text(if (n > 0) "${t.practiceMistakes} · ${t.mistakesSaved(n)}" else t.noSavedMistakes)
                }
            }
            item { Text(t.subjectsHeader, style = MaterialTheme.typography.titleLarge) }
            items(SubjectDef.entries.chunked(2)) { pair ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    pair.forEach { s ->
                        val list = catalog.bySubject[s.id].orEmpty()
                        SubjectCard(
                            subject = s, lang = lang, total = list.size,
                            mastered = masteredIn(catalog, s, progress), best = progress.bestPercent(s.id),
                            available = list.isNotEmpty(), compact = true,
                            modifier = Modifier.weight(1f).heightIn(min = 200.dp),
                            onClick = { onOpenSubject(s.id) },
                        )
                    }
                }
            }
        }
    }
}
