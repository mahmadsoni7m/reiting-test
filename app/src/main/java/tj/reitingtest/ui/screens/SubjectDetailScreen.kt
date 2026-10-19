package tj.reitingtest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.reitingtest.CatalogState
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.SubjectDef
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.ChoiceChip
import tj.reitingtest.ui.components.EmptyBox

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    state: CatalogState,
    lang: Lang,
    onBack: () -> Unit,
    onStart: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    val t = LocalT.current
    val subject = SubjectDef.byId(subjectId)
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t.back)
            }
            Text(subject?.title(lang) ?: "", style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f).padding(end = 12.dp))
        }
        CatalogGate(state, onRetry) { catalog ->
            val available = catalog.bySubject[subjectId].orEmpty().size
            if (subject == null || available == 0) {
                EmptyBox(t.emptySubject)
            } else {
                val options = (listOf(10, 20, 50).filter { it <= available } + 0).distinct()
                var selected by rememberSaveable { mutableStateOf(options.first()) }
                if (selected !in options) selected = options.first()
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(t.questionsCount(available), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(t.chooseTest, style = MaterialTheme.typography.titleMedium)
                    options.forEach { n ->
                        ChoiceChip(
                            text = if (n == 0) t.allQuestions(available) else t.nQuestions(n),
                            selected = selected == n,
                            onClick = { selected = n },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onStart(selected) }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text(t.start, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
