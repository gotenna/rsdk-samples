package com.gotenna.android.rsdksample.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.gotenna.android.rsdksample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Convenience function for navigating to another Fragment
 */
fun Fragment.navigateTo(fragment: Fragment, tag: String, isReplace: Boolean = false) = parentFragmentManager.beginTransaction().apply {
    when {
        isReplace -> replace(R.id.mainContainer, fragment, tag)
        else -> add(R.id.mainContainer, fragment, tag)
    }
    addToBackStack(tag)
    commit()
}

/**
 * Convenience function for adding ComposeView to a Fragment
 */
fun Fragment.Screen(content: @Composable () -> Unit) =
    ComposeView(requireContext()).apply {
        // Dispose of the Composition when the view's LifecycleOwner is destroyed
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MaterialTheme {
                content()
            }
        }
    }

/**
 * Wrapper to show a loading indicator while the code inside is executing.
 * Uses a app-wide loading indicator (which blocks the UI).
 */
suspend fun withLoading(
    block: suspend () -> Unit,
) {
    Global.loadingState.update { true }
    block()
    Global.loadingState.update { false }
}

/**
 * Convenience function for launching coroutines with loading indicator
 */
fun launchWithLoading(
    block: suspend () -> Unit,
) = Global.applicationScope.launch(Dispatchers.Main) {
    withLoading(block = block)
}

fun MutableSharedFlow<Unit>.trigger() = Global.applicationScope.launch(Dispatchers.IO) {
    emit(Unit)
}