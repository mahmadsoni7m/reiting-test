package tj.reitingtest.ui.screens

import androidx.compose.runtime.Composable
import tj.reitingtest.CatalogState
import tj.reitingtest.data.Catalog
import tj.reitingtest.ui.LocalT
import tj.reitingtest.ui.components.ErrorBox
import tj.reitingtest.ui.components.LoadingBox

/** Ҳолатҳои боркунӣ ва хатогиро дар як ҷо идора мекунад. */
@Composable
fun CatalogGate(state: CatalogState, onRetry: () -> Unit, content: @Composable (Catalog) -> Unit) {
    val t = LocalT.current
    when (state) {
        CatalogState.Loading -> LoadingBox(t.loading)
        is CatalogState.Error -> ErrorBox(t.errorTitle, state.message, t.retry, onRetry)
        is CatalogState.Ready -> content(state.catalog)
    }
}
