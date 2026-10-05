package com.social.vitadrop.domain.usecase


import com.social.vitadrop.domain.model.NewRequest
import com.social.vitadrop.domain.repository.RequestRepository

class CreateRequestUseCase(
    private val repository: RequestRepository
) {

    /** Returns the new request id, or a failure with a message that is safe to show to the user. */
    suspend operator fun invoke(request: NewRequest): Result<String> {

        val cleaned = request.copy(
            patientName = request.patientName.trim(),
            bloodGroup = request.bloodGroup.trim().uppercase(),
            contactPerson = request.contactPerson.trim(),
            contactPhone = request.contactPhone.trim(),
            city = request.city.trim(),
            description = request.description.trim()
        )

        val error = when {
            cleaned.patientName.isBlank() -> "Patient name is required"
            cleaned.bloodGroup !in VALID_BLOOD_GROUPS ->
                "Enter a valid blood group (A+, A-, B+, B-, AB+, AB-, O+, O-)"
            cleaned.unitsRequired < 1 -> "Units required must be at least 1"
            cleaned.contactPhone.isBlank() -> "Contact number is required"
            else -> null
        }

        if (error != null) return Result.failure(IllegalArgumentException(error))

        return repository.createRequest(cleaned)
    }

    private companion object {
        val VALID_BLOOD_GROUPS = setOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    }
}