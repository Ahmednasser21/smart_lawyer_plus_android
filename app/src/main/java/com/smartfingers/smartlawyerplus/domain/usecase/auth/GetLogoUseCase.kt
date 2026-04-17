package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetLogoUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<String> = repository.getLogo()
}