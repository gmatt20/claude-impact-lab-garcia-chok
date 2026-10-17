package org.bridge.app.domain

import org.bridge.app.api.BridgeApi
import org.bridge.app.api.BridgeJson
import org.bridge.app.api.Bundle
import org.bridge.app.fetchBundledText
import org.bridge.app.readCache
import org.bridge.app.writeCache

/** Where the bundle in use came from. */
enum class BundleSource { NETWORK, CACHE, BUNDLED_SEED }

data class LoadedBundle(val bundle: Bundle, val source: BundleSource)

/**
 * Backend first (when configured), then the last copy saved on this device, then the event seed
 * file shipped with the app. After one visit the whole journey works with no network.
 */
class BundleRepository(private val api: BridgeApi?) {
    private val key = "bridge.bundle"

    suspend fun load(): LoadedBundle? {
        val client = api
        if (client != null) {
            val fresh = runCatching { client.bundle() }.getOrNull()?.takeIf { it.resources.isNotEmpty() }
            if (fresh != null) {
                runCatching { writeCache(key, BridgeJson.encodeToString(Bundle.serializer(), fresh)) }
                return LoadedBundle(fresh, BundleSource.NETWORK)
            }
            decode(readCache(key))?.let { return LoadedBundle(it, BundleSource.CACHE) }
        }
        return decode(fetchBundledText(SEED_PATH))?.let { LoadedBundle(it, BundleSource.BUNDLED_SEED) }
    }

    private fun decode(json: String?): Bundle? =
        json?.let { runCatching { BridgeJson.decodeFromString(Bundle.serializer(), it) }.getOrNull() }
            ?.takeIf { it.resources.isNotEmpty() }

    private companion object {
        const val SEED_PATH = "seed/oc-resources.seed.json"
    }
}
