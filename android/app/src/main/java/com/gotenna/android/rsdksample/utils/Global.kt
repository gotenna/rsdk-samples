package com.gotenna.android.rsdksample.utils

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Global variables
 */
object Global {
    val applicationScope = MainScope()
    val loadingState = MutableStateFlow(false)
}

