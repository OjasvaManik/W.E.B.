package net.ojm.web_backend.controller.user.admin

import net.ojm.web_backend.domain.dto.article.request.CreateCategoryRequest
import net.ojm.web_backend.domain.dto.article.response.ArticleStatusResponse
import net.ojm.web_backend.domain.dto.user.request.ListUserRequest
import net.ojm.web_backend.domain.dto.user.response.ListUserResponse
import net.ojm.web_backend.domain.entity.article.StatusEntity
import net.ojm.web_backend.extensions.article.toCategoryEntity
import net.ojm.web_backend.service.article.ArticleService
import net.ojm.web_backend.service.article.impl.ArticleServiceImpl
import net.ojm.web_backend.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/web/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    val userService: UserService,
    val articleService: ArticleService
) {

    @GetMapping(path = ["/users"])
    fun getUsers(@ModelAttribute request: ListUserRequest): ResponseEntity<List<ListUserResponse>> {
        return ResponseEntity.ok(userService.searchUser(request.userName))
    }

    @PutMapping("/{userId}/toggle-ban")
    fun toggleUserBanStatus(@PathVariable userId: UUID): ResponseEntity<String> {
        return try {
            val updatedUser = userService.toggleBanStatus(userId)
            val status = if (updatedUser.isBanned) "banned" else "unbanned"
            ResponseEntity.ok("User ${updatedUser.userName} has been $status.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Failed to toggle ban status: ${e.message}")
        }
    }

    @PostMapping(path = ["/categories"])
    fun createCategory(@RequestBody request: CreateCategoryRequest): ResponseEntity<String> {
        return articleService.createCategory(request);
    }

    @PutMapping(path = ["/articles/{articleId}/approve"])
    fun approveArticle(@PathVariable articleId: UUID): ResponseEntity<ArticleStatusResponse> {
        return articleService.approveArticle(articleId);
    }

    @PutMapping(path = ["/articles/{articleId}/reject"])
    fun rejectArticle(@PathVariable articleId: UUID): ResponseEntity<ArticleStatusResponse> {
        return articleService.rejectArticle(articleId);
    }

}