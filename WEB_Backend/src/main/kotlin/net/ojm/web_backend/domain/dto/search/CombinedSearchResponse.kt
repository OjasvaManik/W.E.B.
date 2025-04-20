package net.ojm.web_backend.domain.dto.search

import java.util.UUID

data class CombinedSearchResponse(
    val id: UUID,
    val title: String,
    val content: String,
    val userId: UUID,
    val userName: String,
    val type: String,  // "ARTICLE" or "POST"
    val source: String? = null,  // Only for articles
    val commentCount: Int? = null  // Only for posts
)
