package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.model.LoggedUser
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class GetCachedUserUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): LoggedUser? = repository.getCachedUser()
}