package net.ojm.web_backend.domain.dto.search

data class SearchRequest(
    val searchTerm: String? = null,
    val type: String? = null,  // Optional: "ARTICLE", "POST", or null for both
    val limit: Int = 20,
    val offset: Int = 0
)
