package org.bridge.app.api

import kotlinx.serialization.json.Json

val BridgeJson = Json { ignoreUnknownKeys = true; explicitNulls = false }
