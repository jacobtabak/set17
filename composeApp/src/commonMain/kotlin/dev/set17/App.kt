package dev.set17

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.set17.earlygame.ui.CompDetailPanel
import dev.set17.earlygame.ui.EarlyGameScreen
import dev.set17.earlygame.ui.TftColors
import dev.set17.tftacademy.api.TftAcademyRepository
import dev.set17.tftacademy.db.DriverFactory
import dev.set17.tftacademy.network.TftAcademyClient
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

@Serializable
object EarlyGameRoute

@Serializable
data class CompDetailRoute(val slug: String)

private val WIDE_BREAKPOINT = 600.dp

@Composable
fun App(driverFactory: DriverFactory) {
    val repo = remember {
        TftAcademyRepository(TftAcademyClient(HttpClient()), driverFactory)
    }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = EarlyGameRoute,
        modifier = Modifier.fillMaxSize().background(TftColors.background),
    ) {
        composable<EarlyGameRoute> { backStackEntry ->
            val restoreSlug = backStackEntry.savedStateHandle
                .get<String>("restoreSlug")

            EarlyGameScreen(
                repo = repo,
                onNavigateToComp = { slug ->
                    navController.navigate(CompDetailRoute(slug))
                },
                restoreSlug = restoreSlug,
            )
        }
        composable<CompDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CompDetailRoute>()

            BoxWithConstraints {
                val isWide = maxWidth >= WIDE_BREAKPOINT
                LaunchedEffect(isWide) {
                    if (isWide) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("restoreSlug", route.slug)
                        navController.popBackStack()
                    }
                }

                if (!isWide) {
                    val comp = remember(route.slug) { repo.getComp(route.slug) }
                    if (comp != null) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(TftColors.surface),
                        ) {
                            CompDetailPanel(comp, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
