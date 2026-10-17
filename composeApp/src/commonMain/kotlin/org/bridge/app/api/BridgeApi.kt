package org.bridge.app.api

interface BridgeApi {
    suspend fun bundle(): Bundle
    suspend fun intake(request: IntakeRequest): IntakeResponse

    /** True when responses are hard-coded development data, not verified records. */
    val isMock: Boolean
}
