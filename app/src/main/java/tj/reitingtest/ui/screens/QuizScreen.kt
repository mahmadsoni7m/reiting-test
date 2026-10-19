package tj.reitingtest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tj.reitingtest.domain.QuizSession
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.ProgressBar
import tj.reitingtest.ui.theme.AppColors

private enum class OptionState { IDLE, CORRECT, WRONG, DIM }

@Composable
fun QuizScreen(
    session: QuizSession,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    onExit: () -> Unit,
) {
    val t = LocalT.current
    var confirmExit by rememberSaveable { mutableStateOf(false) }
    BackHandler { confirmExit = true }

    val prepared = session.current
    val answered = session.currentAnswer
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { confirmExit = true }) {
                Icon(Icons.Default.Close, contentDescription = t.exit)
            }
            Text(
                "${session.currentIndex + 1}/${session.total}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
        }
        ProgressBar(
            (session.currentIndex + (if (answered != null) 1 else 0)).toFloat() / session.total,
            MaterialTheme.colorScheme.primary,
            Modifier.padding(horizontal = 16.dp),
        )
        Column(
            Modifier.weight(1f).verticalScroll(scroll).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(prepared.question.text, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(18.dp))
            }
            prepared.optionOrder.forEachIndexed { index, key ->
                val state = when {
                    answered == null -> OptionState.IDLE
                    key == prepared.question.correctAnswer -> OptionState.CORRECT
                    key == answered -> OptionState.WRONG
                    else -> OptionState.DIM
                }
                val letter = tj.reitingtest.domain.OPTION_KEYS[index]
                AnswerOption(letter, prepared.textOf(key), state, answered == null) { onSelect(key) }
            }
            AnimatedVisibility(visible = answered != null) {
                val ok = answered != null && prepared.isCorrect(answered)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = (if (ok) AppColors.correct else AppColors.wrong).copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, (if (ok) AppColors.correct else AppColors.wrong).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(if (ok) t.correct else t.wrong, style = MaterialTheme.typography.titleMedium,
                            color = if (ok) AppColors.correct else AppColors.wrong)
                        Text(t.explanation, style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(prepared.question.explanation, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        Button(
            onClick = { if (session.isLastQuestion) onFinish() else onNext() },
            enabled = answered != null,
            modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 56.dp),
        ) {
            Text(if (session.isLastQuestion) t.finish else t.next, style = MaterialTheme.typography.titleMedium)
        }
    }

    if (confirmExit) {
        AlertDialog(
            onDismissRequest = { confirmExit = false },
            title = { Text(t.exitTitle) },
            text = { Text(t.exitText) },
            confirmButton = { TextButton(onClick = { confirmExit = false; onExit() }) { Text(t.exit) } },
            dismissButton = { TextButton(onClick = { confirmExit = false }) { Text(t.stay) } },
        )
    }
}

@Composable
private fun AnswerOption(letter: String, text: String, state: OptionState, enabled: Boolean, onClick: () -> Unit) {
    val t = LocalT.current
    val scheme = MaterialTheme.colorScheme
    val container by animateColorAsState(
        when (state) {
            OptionState.CORRECT -> AppColors.correct.copy(alpha = 0.22f)
            OptionState.WRONG -> AppColors.wrong.copy(alpha = 0.22f)
            else -> scheme.surface
        }, label = "container",
    )
    val border by animateColorAsState(
        when (state) {
            OptionState.CORRECT -> AppColors.correct
            OptionState.WRONG -> AppColors.wrong
            OptionState.DIM -> scheme.outline.copy(alpha = 0.3f)
            OptionState.IDLE -> scheme.outline.copy(alpha = 0.7f)
        }, label = "border",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .semantics { contentDescription = t.answerLabel(letter, text) }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = container,
        border = BorderStroke(2.dp, border),
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(34.dp).background(
                    when (state) {
                        OptionState.CORRECT -> AppColors.correct
                        OptionState.WRONG -> AppColors.wrong
                        else -> scheme.surfaceVariant
                    }, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    OptionState.CORRECT -> Icon(Icons.Default.Check, null, tint = Color.White)
                    OptionState.WRONG -> Icon(Icons.Default.Close, null, tint = Color.White)
                    else -> Text(letter, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.size(12.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (state == OptionState.DIM) scheme.onSurface.copy(alpha = 0.55f) else scheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
