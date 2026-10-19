package tj.reitingtest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.reitingtest.CatalogState
import tj.reitingtest.domain.AppSettings
import tj.reitingtest.domain.Lang
import tj.reitingtest.domain.NativeEngine
import tj.reitingtest.domain.ThemeMode
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.ChoiceChip
import tj.reitingtest.ui.theme.AppColors

@Composable
fun SettingsScreen(
    settings: AppSettings,
    catalog: CatalogState,
    onTheme: (ThemeMode) -> Unit,
    onLang: (Lang) -> Unit,
    onReset: () -> Unit,
) {
    val t = LocalT.current
    var confirm by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(t.settingsTitle, style = MaterialTheme.typography.headlineSmall)

        Text(t.theme, style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(ThemeMode.SYSTEM to t.themeSystem, ThemeMode.DARK to t.themeDark, ThemeMode.LIGHT to t.themeLight)
                .forEach { (mode, label) ->
                    ChoiceChip(label, settings.theme == mode, { onTheme(mode) }, Modifier.weight(1f))
                }
        }

        Text(t.language, style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Lang.entries.forEach { l ->
                ChoiceChip(l.label, settings.lang == l, { onLang(l) }, Modifier.weight(1f))
            }
        }

        Text(t.dataHeader, style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = { confirm = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
            Text(t.resetProgress)
        }

        Text(t.diagnostics, style = MaterialTheme.typography.titleMedium)
        when (catalog) {
            is CatalogState.Ready -> {
                val c = catalog.catalog
                val problems = c.issues.size + c.loadErrors.size
                if (problems == 0) {
                    Text(t.diagOk(c.all.size), color = AppColors.correct)
                } else {
                    Text(t.diagIssues(problems), color = AppColors.wrong)
                    (c.loadErrors.map { (k, v) -> "$k: $v" } + c.issues.map { it.toString() }).take(30).forEach {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            is CatalogState.Error -> Text(catalog.message, color = AppColors.wrong)
            CatalogState.Loading -> Text(t.loading)
        }

        Text(t.about, style = MaterialTheme.typography.titleMedium)
        Text(t.aboutText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("reiting-test 1.0.0 · tj.reitingtest", color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
        Text(t.engine(NativeEngine.version()), color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
    }

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(t.resetTitle) },
            text = { Text(t.resetText) },
            confirmButton = { TextButton(onClick = { confirm = false; onReset() }) { Text(t.confirm) } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(t.cancel) } },
        )
    }
}
