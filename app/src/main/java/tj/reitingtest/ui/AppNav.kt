package tj.reitingtest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import tj.reitingtest.MainViewModel
import tj.reitingtest.ui.screens.HomeScreen
import tj.reitingtest.ui.screens.ResultScreen
import tj.reitingtest.ui.screens.ResultsScreen
import tj.reitingtest.ui.screens.ReviewScreen
import tj.reitingtest.ui.screens.QuizScreen
import tj.reitingtest.ui.screens.SettingsScreen
import tj.reitingtest.ui.screens.SubjectDetailScreen
import tj.reitingtest.ui.screens.SubjectsScreen

private data class Tab(val route: String, val icon: ImageVector, val label: (T) -> String)

private val tabs = listOf(
    Tab("home", Icons.Default.Home) { it.navHome },
    Tab("subjects", Icons.AutoMirrored.Filled.List) { it.navSubjects },
    Tab("results", Icons.Default.Star) { it.navResults },
    Tab("settings", Icons.Default.Settings) { it.navSettings },
)

@Composable
fun AppNav(vm: MainViewModel) {
    val t = LocalT.current
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val catalog by vm.catalog.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val progress by vm.progress.collectAsStateWithLifecycle()
    val quiz by vm.quiz.collectAsStateWithLifecycle()

    fun goHome() {
        nav.navigate("home") { popUpTo(nav.graph.findStartDestination().id) { inclusive = false }; launchSingleTop = true }
    }

    Scaffold(
        bottomBar = {
            if (route in tabs.map { it.route }) {
                NavigationBar(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label(t)) },
                            label = { Text(tab.label(t)) },
                            colors = NavigationBarItemDefaults.colors(),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            NavHost(nav, startDestination = "home") {
                composable("home") {
                    HomeScreen(catalog, progress, settings.lang,
                        onOpenSubject = { nav.navigate("subject/$it") },
                        onPracticeMistakes = { if (vm.startMistakes()) nav.navigate("quiz") },
                        onRetry = vm::loadCatalog)
                }
                composable("subjects") {
                    SubjectsScreen(catalog, progress, settings.lang,
                        onOpenSubject = { nav.navigate("subject/$it") }, onRetry = vm::loadCatalog)
                }
                composable("results") { ResultsScreen(progress, settings.lang) }
                composable("settings") {
                    SettingsScreen(settings, catalog, vm::setTheme, vm::setLang, vm::resetProgress)
                }
                composable("subject/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    SubjectDetailScreen(id, catalog, settings.lang,
                        onBack = { nav.popBackStack() },
                        onStart = { count -> if (vm.startQuiz(id, count)) nav.navigate("quiz") },
                        onRetry = vm::loadCatalog)
                }
                composable("quiz") {
                    val s = quiz
                    if (s == null) {
                        LaunchedEffect(Unit) { goHome() }
                    } else {
                        QuizScreen(s, vm::select, vm::next,
                            onFinish = { if (vm.finish()) nav.navigate("result") { popUpTo("quiz") { inclusive = true } } },
                            onExit = { vm.abandonQuiz(); nav.popBackStack() })
                    }
                }
                composable("result") {
                    val s = quiz
                    if (s == null) {
                        LaunchedEffect(Unit) { goHome() }
                    } else {
                        ResultScreen(s,
                            onReview = { nav.navigate("review") },
                            onRetake = { if (vm.retake()) nav.navigate("quiz") { popUpTo("result") { inclusive = true } } },
                            onHome = { vm.abandonQuiz(); goHome() })
                    }
                }
                composable("review") {
                    val s = quiz
                    if (s == null) {
                        LaunchedEffect(Unit) { goHome() }
                    } else {
                        ReviewScreen(s) { nav.popBackStack() }
                    }
                }
            }
        }
    }
}
