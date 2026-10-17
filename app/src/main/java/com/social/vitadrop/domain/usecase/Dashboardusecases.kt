package com.social.vitadrop.domain.usecase

import com.social.vitadrop.domain.model.DashboardStats
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// BUSINESS RULE: a request still needs donors unless it was finished or cancelled.
// ASSUMPTION: adjust this list to the status values your app really writes.
// ("pending" is created by the app, "accepted" is set by the onResponseCreated Cloud Function.)
private val CLOSED_STATUSES = setOf("completed", "cancelled", "closed", "expired", "fulfilled")

private const val MAX_EMERGENCY_ON_DASHBOARD = 10

internal fun RequestModel.isOpen(): Boolean =
    status.trim().lowercase() !in CLOSED_STATUSES

/**
 * The three numbers at the top of the dashboard, loaded in parallel.
 * They are SEPARATE from the emergency list now: if counting fails, the emergency list
 * still works (before, one failure blanked the whole dashboard).
 */
class GetDashboardStatsUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardStats> = coroutineScope {

        val donors = async { repository.getDonorCount() }
        val hospitals = async { repository.getHospitalCount() }
        val requests = async { repository.getRequestCount() }

        val donorsCount = donors.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardStats>(it) }
        val hospitalsCount = hospitals.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardStats>(it) }
        val requestsCount = requests.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardStats>(it) }

        Result.success(DashboardStats(donorsCount, hospitalsCount, requestsCount))
    }
}

/**
 * LIVE list of open emergency requests (newest first, at most 10).
 * A new request, or a request that becomes "accepted", appears on the dashboard
 * without pressing anything.
 */
class ObserveEmergencyRequestsUseCase(
    private val repository: DashboardRepository
) {
    operator fun invoke(): Flow<List<RequestModel>> =
        repository.observeEmergencyRequests().map { requests ->
            requests
                .filter { it.isOpen() }
                .take(MAX_EMERGENCY_ON_DASHBOARD)
        }
}

/** Open (not completed / cancelled) requests for the "Blood Requests" list screen. */
class GetOpenRequestsUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<List<RequestModel>> =
        repository.getAllRequests().map { requests -> requests.filter { it.isOpen() } }
}