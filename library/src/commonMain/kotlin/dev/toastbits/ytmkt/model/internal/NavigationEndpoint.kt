package dev.toastbits.ytmkt.model.internal

import dev.toastbits.ytmkt.model.external.*
import dev.toastbits.ytmkt.model.external.mediaitem.*
import kotlinx.serialization.Serializable

@Serializable
data class NavigationEndpoint(
    val watchEndpoint: WatchEndpoint?,
    val browseEndpoint: BrowseEndpoint?,
    val searchEndpoint: SearchEndpoint?,
    val watchPlaylistEndpoint: WatchPlaylistEndpoint?,
    val channelCreationFormEndpoint: ChannelCreationFormEndpoint?,
    val commandMetadata: CommandMetadata?
) {
    fun getMediaItem(): YtmMediaItem? {
        if (watchEndpoint != null) {
            if (watchEndpoint.videoId != null) {
                return YtmSong(YtmSong.cleanId(watchEndpoint.videoId))
            }
            else if (watchEndpoint.playlistId != null) {
                return YtmPlaylist(YtmPlaylist.cleanId(watchEndpoint.playlistId))
            }
        }
        if (browseEndpoint != null) {
            browseEndpoint.getMediaItem()?.also { return it }
        }
        if (watchPlaylistEndpoint != null) {
            return YtmPlaylist(YtmPlaylist.cleanId(watchPlaylistEndpoint.playlistId))
        }
        return null
    }

    fun getViewMore(item: YtmMediaItem): YoutubePage? {
        if (browseEndpoint != null) {
            browseEndpoint.getViewMore(item).also { return it }
        }
        return getMediaItem()?.let { MediaItemYoutubePage(it, null, item) }
    }
}

@Serializable
data class ChannelCreationFormEndpoint(val channelCreationToken: String)

@Serializable
data class CommandMetadata(val webCommandMetadata: WebCommandMetadata?)

@Serializable
data class WebCommandMetadata(val webPageType: String?)
