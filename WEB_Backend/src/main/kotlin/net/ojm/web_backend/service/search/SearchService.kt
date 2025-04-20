package net.ojm.web_backend.service.search

import net.ojm.web_backend.domain.dto.search.CombinedSearchResponse

interface SearchService {

    fun searchByTitle(searchTerm: String?): List<CombinedSearchResponse>

}