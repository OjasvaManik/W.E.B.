package net.ojm.web_backend.service.article.impl

import jakarta.persistence.EntityNotFoundException
import net.ojm.web_backend.domain.dto.article.request.CreateArticleRequest
import net.ojm.web_backend.domain.dto.article.request.CreateCategoryRequest
import net.ojm.web_backend.domain.entity.article.StatusEntity
import net.ojm.web_backend.domain.entity.article.StatusTypeEnum
import net.ojm.web_backend.repo.article.ArticleImageRepo
import net.ojm.web_backend.repo.article.ArticleRepo
import net.ojm.web_backend.repo.article.CategoryRepo
import net.ojm.web_backend.repo.article.StatusRepo
import net.ojm.web_backend.repo.user.UserRepo
import net.ojm.web_backend.service.article.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import jakarta.persistence.EntityManager
import net.ojm.web_backend.domain.dto.article.request.UpdateArticleRequest
import net.ojm.web_backend.domain.dto.article.response.*
import net.ojm.web_backend.extensions.article.*
import net.ojm.web_backend.extensions.user.toArticleUserResponse

@Service
class ArticleServiceImpl (
    val articleRepo: ArticleRepo,
    val statusRepo: StatusRepo,
    val categoryRepo: CategoryRepo,
    val articleImageRepo: ArticleImageRepo,
    val userRepo: UserRepo,
) : ArticleService {

    @Transactional
    override fun createArticle(request: CreateArticleRequest): ResponseEntity<CreateArticleResponse> {
        val user = userRepo.findById(request.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val article = request.toArticleEntity(user)

        val statusEntity = StatusEntity(
            article = article,
            status = StatusTypeEnum.PENDING
        )
        article.status = statusEntity

        val createdArticle = articleRepo.save(article)

        // Get categories once
        val categories = categoryRepo.findAllByCategoryIdIn(request.categoryIds)
        categories.forEach {
            it.articles.add(createdArticle)
        }
        categoryRepo.saveAll(categories) // Save all updated categories at once

        // Map article to response DTO
        val response = CreateArticleResponse(
            articleId = createdArticle.articleId,
            articleTitle = createdArticle.articleTitle,
            articleContent = createdArticle.articleContent,
            articleSource = createdArticle.articleSource,
            categoryIds = categories.map { it.categoryId },
            userId = createdArticle.userId.userId
        )

        return ResponseEntity.ok(response)
    }


    @Transactional
    override fun updateArticle(articleId: UUID, request: UpdateArticleRequest): ResponseEntity<String> {
        val existingArticle = articleRepo.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found") }

        val newUser = userRepo.findById(request.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val newCategories = categoryRepo.findAllByCategoryIdIn(request.categoryIds)
            .also {
                if (it.size != request.categoryIds.size) {
                    throw IllegalArgumentException("One or more categories not found")
                }
            }

        existingArticle.categories
            .filter { it !in newCategories }
            .forEach { oldCategory ->
                oldCategory.articles.remove(existingArticle)
                categoryRepo.save(oldCategory)
            }

        newCategories
            .filter { it !in existingArticle.categories }
            .forEach { newCategory ->
                newCategory.articles.add(existingArticle)
                categoryRepo.save(newCategory)
            }

        val updatedArticle = existingArticle.copy(
            articleId = articleId,
            articleTitle = request.articleTitle,
            articleContent = request.articleContent,
            articleSource = request.articleSource,
            userId = newUser
        ).apply {
            categories.clear()
            categories.addAll(newCategories)

            status = status?.copy(
                status = StatusTypeEnum.PENDING
            ) ?: StatusEntity(
                article = this,
                status = StatusTypeEnum.PENDING
            )
        }

        articleRepo.save(updatedArticle)
        return ResponseEntity.ok("Updated")
    }

    @Transactional
    override fun createCategory(request: CreateCategoryRequest): ResponseEntity<String> {
        categoryRepo.save(request.toCategoryEntity());
        return ResponseEntity.ok("Created");
    }

    override fun listCategories(): List<ListCategoryResponse> {
        return categoryRepo.findAll().map {
            it.toListCategoryResponse()
        }
    }

    override fun listArticles(): List<ListArticleResponse> {
        return articleRepo.findAll().map {
            ListArticleResponse(
                articleId = it.articleId,
                articleTitle = it.articleTitle,
                articleContent = it.articleContent,
                articleSource = it.articleSource,
                userId = it.userId.userId,
                articleStatus = it.status?.toStatusResponse(),
                categoryName = it.categories.map { it1 -> it1.categoryName }
            )
        }
    }

    override fun listArticlesByCategory(categoryId: UUID): List<ListArticleResponse> {
        return articleRepo.findByCategoriesCategoryId(categoryId)
            .map { ListArticleResponse(
                articleId = it.articleId,
                articleTitle = it.articleTitle,
                articleContent = it.articleContent,
                articleSource = it.articleSource,
                userId = it.userId.userId,
                articleStatus = it.status?.toStatusResponse(),
                categoryName = it.categories.map { it1 -> it1.categoryName }
            ) }
    }

    @Transactional
    override fun approveArticle(articleId: UUID): ResponseEntity<ArticleStatusResponse> {
        val article = statusRepo.findById(articleId)
            .orElseThrow { throw EntityNotFoundException("Article not found with ID: $articleId") }

        val updatedStatus = article.copy(status = StatusTypeEnum.APPROVED)
        val savedStatus = statusRepo.save(updatedStatus)
        return ResponseEntity.ok(savedStatus.toArticleStatusResponse());
    }

    @Transactional
    override fun rejectArticle(articleId: UUID): ResponseEntity<ArticleStatusResponse> {
        val article = statusRepo.findById(articleId)
            .orElseThrow { throw EntityNotFoundException("Article not found with ID: $articleId") }

        val updatedStatus = article.copy(status = StatusTypeEnum.REJECTED)
        val savedStatus = statusRepo.save(updatedStatus);
        return ResponseEntity.ok(savedStatus.toArticleStatusResponse());
    }

    override fun viewArticle(articleId: UUID): ResponseEntity<ViewArticleResponse> {
        val article = articleRepo.findById(articleId)
            .orElseThrow { EntityNotFoundException("Article not found with ID: $articleId") }

        val articleImageName = articleImageRepo.findArticleImageNameByArticle_ArticleId(articleId);

        val response = article.status?.let {
            ViewArticleResponse(
                articleId = article.articleId,
                articleImageUrl = articleImageName?.articleImageName,
                articleTitle = article.articleTitle,
                articleContent = article.articleContent,
                articleSource = article.articleSource,
                articleStatus = it.toStatusResponse(),
                user = userRepo.findById(article.userId.userId)
                    .orElseThrow { EntityNotFoundException("User not found") }
                    .toArticleUserResponse()
            )
        }

        return ResponseEntity.ok(response)
    }

}