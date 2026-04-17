package com.smartfingers.smartlawyerplus.domain.usecase.auth

import com.smartfingers.smartlawyerplus.domain.model.AppConfig
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class InitAppUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(link: String, code: String): Result<AppConfig> =
        repository.initApp(link, code)
}