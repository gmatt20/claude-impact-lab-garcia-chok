package org.bridge.app.domain

import org.bridge.app.api.BridgeApi
import org.bridge.app.api.Bundle
import org.bridge.app.api.BridgeJson
import org.bridge.app.readCache
import org.bridge.app.writeCache

/** Where the bundle in use came from. */
enum class BundleSource { NETWORK, CACHE }

data class LoadedBundle(val bundle: Bundle, val source: BundleSource)

/**
 * Network-first, cache-fallback. A successful fetch refreshes the on-device copy,
 * so after one online visit the whole journey works with no network.
 */
class BundleRepository(private val api: BridgeApi) {
    private val key = if (api.isMock) "bridge.bundle.mock" else "bridge.bundle"

    suspend fun load(): LoadedBundle? {
        val fresh = runCatching { api.bundle() }.getOrNull()
        if (fresh != null) {
            runCatching { writeCache(key, BridgeJson.encodeToString(Bundle.serializer(), fresh)) }
            return LoadedBundle(fresh, BundleSource.NETWORK)
        }
        val cached = runCatching { readCache(key)?.let { BridgeJson.decodeFromString(Bundle.serializer(), it) } }.getOrNull()
        return cached?.let { LoadedBundle(it, BundleSource.CACHE) }
    }
}
