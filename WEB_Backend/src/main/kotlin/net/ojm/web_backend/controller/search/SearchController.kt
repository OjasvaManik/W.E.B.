package net.ojm.web_backend.controller.search

import net.ojm.web_backend.domain.dto.search.CombinedSearchResponse
import net.ojm.web_backend.service.search.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/web/search")
class SearchController(
    private val searchService: SearchService
) {
    @GetMapping
    fun search(@RequestParam(required = false) searchTerm: String?): ResponseEntity<List<CombinedSearchResponse>> {
        return ResponseEntity.ok(searchService.searchByTitle(searchTerm))
    }
}