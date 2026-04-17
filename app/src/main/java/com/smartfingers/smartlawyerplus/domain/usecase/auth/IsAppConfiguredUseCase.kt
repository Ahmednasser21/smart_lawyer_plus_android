package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class IsAppConfiguredUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean = repository.isAppConfigured()
}