package net.ojm.web_backend.service.article

import net.ojm.web_backend.domain.dto.article.request.CreateArticleRequest
import net.ojm.web_backend.domain.dto.article.request.CreateCategoryRequest
import net.ojm.web_backend.domain.dto.article.request.UpdateArticleRequest
import net.ojm.web_backend.domain.dto.article.response.*
import net.ojm.web_backend.domain.entity.article.StatusEntity
import org.springframework.http.ResponseEntity
import java.util.UUID

interface ArticleService {

    fun createArticle(request: CreateArticleRequest): ResponseEntity<CreateArticleResponse>

    fun createCategory(request: CreateCategoryRequest): ResponseEntity<String>

    fun listCategories(): List<ListCategoryResponse>

    fun listArticles(): List<ListArticleResponse>

    fun listArticlesByCategory(categoryId: UUID): List<ListArticleResponse>

    fun approveArticle(articleId: UUID): ResponseEntity<ArticleStatusResponse>

    fun viewArticle(articleId: UUID): ResponseEntity<ViewArticleResponse>

    fun rejectArticle(articleId: UUID): ResponseEntity<ArticleStatusResponse>

    fun updateArticle(articleId: UUID, request: UpdateArticleRequest): ResponseEntity<String>

}