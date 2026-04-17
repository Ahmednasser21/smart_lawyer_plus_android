package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.model.AuthSession
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(session: AuthSession, rememberMe: Boolean) =
        repository.saveSession(session, rememberMe)
}