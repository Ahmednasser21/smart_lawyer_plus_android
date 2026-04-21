package com.smartfingers.smartlawyerplus.domain.usecase.sessions

import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import javax.inject.Inject

class GetCourtsUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getCourts()
}

class GetCasesUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getCases()
}

class GetHearingTypesUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getHearingTypes()
}

class GetSubHearingTypesUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getSubHearingTypes()
}

class GetEmployeesUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getEmployees()
}

class GetBranchesUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getBranches()
}

class GetPartiesUseCase @Inject constructor(private val repository: SessionsRepository) {
    suspend operator fun invoke(): Result<List<FilterOption>> = repository.getParties()
}