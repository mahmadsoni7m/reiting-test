package tj.reitingtest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.MISTAKES_ID
import tj.reitingtest.domain.Progress
import tj.reitingtest.domain.SubjectDef
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.EmptyBox
import tj.reitingtest.ui.components.ProgressBar
import tj.reitingtest.ui.components.accentFor
import tj.reitingtest.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultsScreen(progress: Progress, lang: Lang) {
    val t = LocalT.current
    val fmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Text(t.resultsTitle, style = MaterialTheme.typography.headlineSmall) }
        item { Text(t.bestBySubject, style = MaterialTheme.typography.titleMedium) }
        items(SubjectDef.entries) { s ->
            val best = progress.bestPercent(s.id)
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(s.title(lang), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text(if (best != null) "$best%" else "—", style = MaterialTheme.typography.titleMedium)
                }
                ProgressBar((best ?: 0) / 100f, accentFor(s))
            }
        }
        item { Text(t.historyTitle, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp)) }
        if (progress.history.isEmpty()) {
            item { EmptyBox(t.historyEmpty) }
        } else {
            items(progress.history.reversed()) { r ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (r.subjectId == MISTAKES_ID) t.mistakesMode else SubjectDef.byId(r.subjectId)?.title(lang) ?: r.subjectId,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(fmt.format(Date(r.timestamp)), style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${r.percent}%", style = MaterialTheme.typography.titleMedium,
                                color = if (r.percent >= 60) AppColors.correct else AppColors.wrong)
                            Text(t.scoreLine(r.correct, r.total), style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
