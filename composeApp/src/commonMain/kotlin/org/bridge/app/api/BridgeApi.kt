package org.bridge.app.api

interface BridgeApi {
    suspend fun bundle(): Bundle
    suspend fun intake(request: IntakeRequest): IntakeResponse
}
