package tj.reitingtest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6C8CFF), onPrimary = Color(0xFF0B1020),
    primaryContainer = Color(0xFF24306B), onPrimaryContainer = Color(0xFFDCE3FF),
    secondary = Color(0xFF9B7BFF), onSecondary = Color(0xFF0B1020),
    tertiary = Color(0xFF4DD4C0),
    background = Color(0xFF0B1020), onBackground = Color(0xFFE8EBF7),
    surface = Color(0xFF131A33), onSurface = Color(0xFFE8EBF7),
    surfaceVariant = Color(0xFF1B2444), onSurfaceVariant = Color(0xFFB4BCDB),
    outline = Color(0xFF3A4470), error = Color(0xFFFF6B6B),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF3B5BDB), onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE3FF), onPrimaryContainer = Color(0xFF10205E),
    secondary = Color(0xFF6F42C1), onSecondary = Color.White,
    tertiary = Color(0xFF0B8F7D),
    background = Color(0xFFF5F7FF), onBackground = Color(0xFF10152B),
    surface = Color.White, onSurface = Color(0xFF10152B),
    surfaceVariant = Color(0xFFE6EAFB), onSurfaceVariant = Color(0xFF44507A),
    outline = Color(0xFF8A94BD), error = Color(0xFFD92D2D),
)

object AppColors {
    val correct = Color(0xFF22C55E)
    val wrong = Color(0xFFEF4444)
    val subjects = listOf(
        Color(0xFF6C8CFF), Color(0xFF4DD4C0), Color(0xFFFFB454), Color(0xFF5BD97A),
        Color(0xFFFF7A90), Color(0xFF9B7BFF), Color(0xFF45B8FF), Color(0xFFFF8A4C),
    )
}

private val AppTypography = Typography(
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 24.sp),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun ReitingTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
