package tj.reitingtest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tj.reitingtest.domain.QuizSession
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.EmptyBox
import tj.reitingtest.ui.components.StatTile
import tj.reitingtest.ui.theme.AppColors

@Composable
fun ResultScreen(
    session: QuizSession,
    onReview: () -> Unit,
    onRetake: () -> Unit,
    onHome: () -> Unit,
) {
    val t = LocalT.current
    val r = session.result()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(t.resultTitle, style = MaterialTheme.typography.headlineSmall)
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(170.dp)) {
            CircularProgressIndicator(
                progress = { 1f }, modifier = Modifier.fillMaxSize(), strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
            CircularProgressIndicator(
                progress = { r.percent / 100f }, modifier = Modifier.fillMaxSize(), strokeWidth = 12.dp,
                color = if (r.percent >= 60) AppColors.correct else AppColors.wrong,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${r.percent}%", style = MaterialTheme.typography.headlineSmall)
                Text(t.scoreLine(r.correct, r.total), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(t.correctWord, "${r.correct}", Modifier.weight(1f))
            StatTile(t.wrongWord, "${r.wrong}", Modifier.weight(1f))
        }
        if (r.wrong == 0) {
            Text(t.perfect, color = AppColors.correct, textAlign = TextAlign.Center)
        } else {
            Button(onClick = onReview, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                Text(t.reviewMistakes)
            }
        }
        OutlinedButton(onClick = onRetake, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(t.retake) }
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(t.toHome) }
    }
}

@Composable
fun ReviewScreen(session: QuizSession, onBack: () -> Unit) {
    val t = LocalT.current
    val wrong = session.result().wrongItems
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t.back) }
            Text(t.reviewTitle, style = MaterialTheme.typography.titleLarge)
        }
        if (wrong.isEmpty()) {
            EmptyBox(t.noMistakes)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(wrong) { w ->
                    val q = w.item.question
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(q.text, style = MaterialTheme.typography.titleMedium)
                            Text("${t.yourAnswer}: ${w.item.textOf(w.selected)}", color = AppColors.wrong)
                            Text("${t.rightAnswer}: ${w.item.textOf(q.correctAnswer)}", color = AppColors.correct)
                            Text(q.explanation, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
