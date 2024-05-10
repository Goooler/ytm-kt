package dev.toastbits.ytmkt.model

import io.ktor.client.request.HttpRequestBuilder

abstract class AuthenticatedApiEndpoint: ApiEndpoint() {
    protected abstract val auth: ApiAuthenticationState
    override val api: YtmApi get() = auth.api

    suspend fun HttpRequestBuilder.addAuthlessApiHeaders(include: List<String>?) =
        with (api) {
            addUnauthenticatedApiHeaders(include)
        }

    override suspend fun HttpRequestBuilder.addApiHeadersWithAuthenticated(include: List<String>?, non_music_api: Boolean) {
        addApiHeadersWithoutAuthentication(non_music_api = non_music_api)
        auth.addHeadersToRequest(this, include)
    }
}
