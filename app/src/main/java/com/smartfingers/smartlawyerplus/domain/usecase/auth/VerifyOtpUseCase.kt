package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.model.PasswordResetRequest
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(request: PasswordResetRequest): Result<Boolean> =
        repository.verifyOtp(request)
}