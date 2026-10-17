package org.bridge.app.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json

class ApiException(message: String) : Exception(message)

class HttpBridgeApi(private val baseUrl: String) : BridgeApi {
    override val isMock = false

    private val client = HttpClient {
        install(ContentNegotiation) { json(BridgeJson) }
        install(HttpTimeout)
        expectSuccess = false
    }

    override suspend fun bundle(): Bundle {
        val response = client.get(url("/api/bundle")) { timeout { requestTimeoutMillis = 6_000 } }
        if (!response.status.isSuccess()) throw ApiException("HTTP ${response.status.value} for /api/bundle")
        return response.body()
    }

    // Short timeout: a hung spinner is worse than no AI.
    override suspend fun intake(request: IntakeRequest): IntakeResponse {
        val response = client.post(url("/api/intake")) {
            timeout { requestTimeoutMillis = 3_000 }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw ApiException("HTTP ${response.status.value} for /api/intake")
        return response.body()
    }

    private fun url(path: String) = baseUrl.trimEnd('/') + path
}
