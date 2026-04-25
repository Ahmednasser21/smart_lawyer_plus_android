package com.smartfingers.smartlawyerplus.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppErrorState @Inject constructor() {
    private val _error = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val error = _error.asSharedFlow()

    fun showError(message: String) {
        _error.tryEmit(message)
    }
}