package net.ojm.web_backend.controller.article

import jakarta.validation.Valid
import net.ojm.web_backend.domain.dto.article.request.CreateArticleRequest
import net.ojm.web_backend.domain.dto.article.request.UpdateArticleRequest
import net.ojm.web_backend.domain.dto.article.response.CreateArticleResponse
import net.ojm.web_backend.domain.dto.article.response.ListArticleResponse
import net.ojm.web_backend.domain.dto.article.response.ListCategoryResponse
import net.ojm.web_backend.domain.dto.article.response.ViewArticleResponse
import net.ojm.web_backend.service.article.impl.ARTICLE_IMAGE_FOLDER_PATH
import net.ojm.web_backend.service.article.impl.ArticleImageServiceImpl
import net.ojm.web_backend.service.article.impl.ArticleServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@RestController
@RequestMapping("/api/v1/web/wiki")
class WikiController(
    val articleService: ArticleServiceImpl,
    val articleImageService: ArticleImageServiceImpl,
) {

    @PostMapping(path = ["/create"])
    fun createArticle(@RequestBody @Valid request: CreateArticleRequest): ResponseEntity<CreateArticleResponse> {
        println("Article content length: ${request.articleContent.length}")
        try {
            return articleService.createArticle(request)
        } catch (e: Exception) {
            println("Error creating article: ${e.message}")
            e.printStackTrace()
            return ResponseEntity.badRequest().build()
        }
    }

    @PutMapping(path = ["/{articleId}/update"])
    fun updateArticle(@PathVariable("articleId") articleId: UUID, @RequestBody @Valid request: UpdateArticleRequest): ResponseEntity<String> {
        return articleService.updateArticle(articleId, request);
    }

    @GetMapping(path = ["/create/categories"])
    fun listCategories(): List<ListCategoryResponse> {
        return articleService.listCategories();
    }

    @GetMapping
    fun listArticles(): List<ListArticleResponse> {
        return articleService.listArticles();
    }

    @GetMapping(path = ["/view/{articleId}"])
    fun viewArticle(@PathVariable("articleId") articleId: UUID): ResponseEntity<ViewArticleResponse> {
        return articleService.viewArticle(articleId)
    }

    @GetMapping(path = ["/category/{categoryId}"])
    fun listArticlesByCategory(@PathVariable(value = "categoryId") categoryId: UUID): List<ListArticleResponse> {
        return articleService.listArticlesByCategory(categoryId);
    }

    @PutMapping(path = ["/create/upload"])
    fun uploadArticleImage(
        @RequestParam("image") image: MultipartFile,
        @RequestParam("articleId") articleId: UUID
    ): ResponseEntity<String> {
        return try {
            articleImageService.uploadArticleImage(image, articleId)
            ResponseEntity.ok("Image uploaded successfully for article $articleId")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload image: ${e.message} 😩💀")
        }
    }

    @GetMapping("/{articleId}/{filename}")
    fun downloadArticleImage(
        @PathVariable(value = "articleId") articleId: UUID,
        @PathVariable filename: String
    ): ResponseEntity<ByteArray> {
        return try {
            val imageData = articleImageService.downloadArticleImage(articleId, filename)
            val contentType = Files.probeContentType(Paths.get(ARTICLE_IMAGE_FOLDER_PATH + filename))

            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType ?: "application/octet-stream"))
                .body(imageData)
        } catch (e: FileNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

}