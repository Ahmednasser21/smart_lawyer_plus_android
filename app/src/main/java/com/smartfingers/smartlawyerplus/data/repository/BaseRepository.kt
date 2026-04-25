package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.util.AppErrorState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseRepository(
    private val appErrorState: AppErrorState,
) {
    protected suspend fun <T> safeApiCall(
        block: suspend () -> Result<T>,
    ): Result<T> {
        return try {
            block()
        } catch (e: UnknownHostException) {
            val msg = "لا يوجد اتصال بالإنترنت"
            appErrorState.showError(msg)
            Result.Error(msg)
        } catch (e: SocketTimeoutException) {
            val msg = "انتهت مهلة الاتصال، يرجى المحاولة مجدداً"
            appErrorState.showError(msg)
            Result.Error(msg)
        } catch (e: Exception) {
            val msg = e.message ?: "حدث خطأ غير متوقع"
            appErrorState.showError(msg)
            Result.Error(msg)
        }
    }
}