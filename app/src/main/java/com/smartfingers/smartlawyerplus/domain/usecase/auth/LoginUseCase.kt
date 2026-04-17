package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.model.AuthSession
import com.smartfingers.smartlawyerplus.domain.model.LoginCredentials
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(credentials: LoginCredentials): Result<AuthSession> =
        repository.login(credentials)
}