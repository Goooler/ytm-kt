package dev.toastbits.ytmkt.model.internal

import dev.toastbits.ytmkt.impl.youtubei.endpoint.ChipCloudRendererHeader
import dev.toastbits.ytmkt.impl.youtubei.endpoint.YTMGetSongFeedEndpoint
import dev.toastbits.ytmkt.radio.YoutubeiNextResponse
import dev.toastbits.ytmkt.uistrings.YoutubeUiString
import dev.toastbits.ytmkt.endpoint.SongFeedFilterChip
import kotlinx.serialization.Serializable

@Serializable
data class YoutubeiBrowseResponse(
    val contents: Contents?,
    val continuationContents: ContinuationContents?,
    val header: Header?
) {
    val ctoken: String?
        get() = continuationContents?.sectionListContinuation?.continuations?.firstOrNull()?.nextContinuationData?.continuation
            ?: contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.continuations?.firstOrNull()?.nextContinuationData?.continuation

    fun getShelves(has_continuation: Boolean): List<YoutubeiShelf> {
        return if (has_continuation) continuationContents?.sectionListContinuation?.contents ?: emptyList()
        else contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents ?: emptyList()
    }

    fun getHeaderChips(data_language: String): List<SongFeedFilterChip>? =
        contents?.singleColumnBrowseResultsRenderer?.tabs?.first()?.tabRenderer?.content?.sectionListRenderer?.header?.chipCloudRenderer?.chips?.map {
            SongFeedFilterChip(
                YoutubeUiString.Type.FILTER_CHIP.createFromKey(it.chipCloudChipRenderer.text!!.first_text, data_language),
                it.chipCloudChipRenderer.navigationEndpoint.browseEndpoint!!.params!!
            )
        }

    @Serializable
    data class Contents(
        val singleColumnBrowseResultsRenderer: SingleColumnBrowseResultsRenderer?,
        val twoColumnBrowseResultsRenderer: TwoColumnBrowseResultsRenderer?
    )
    @Serializable
    data class SingleColumnBrowseResultsRenderer(val tabs: List<Tab>)
    @Serializable
    data class Tab(val tabRenderer: TabRenderer)
    @Serializable
    data class TabRenderer(val content: Content?)
    @Serializable
    data class Content(val sectionListRenderer: SectionListRenderer?)

    @Serializable
    data class SectionListRenderer(val contents: List<YoutubeiShelf>?, val header: ChipCloudRendererHeader?, val continuations: List<YoutubeiNextResponse.Continuation>?)
    @Serializable
    data class TwoColumnBrowseResultsRenderer(val tabs: List<Tab>, val secondaryContents: SecondaryContents?) {
        @Serializable
        data class SecondaryContents(val sectionListRenderer: SectionListRenderer)
    }

    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer?, val musicPlaylistShelfContinuation: YTMGetSongFeedEndpoint.MusicShelfRenderer?)
}
