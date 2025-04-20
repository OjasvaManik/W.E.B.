package net.ojm.web_backend.service.article.impl

import net.ojm.web_backend.domain.entity.article.ArticleImageEntity
import net.ojm.web_backend.repo.article.ArticleImageRepo
import net.ojm.web_backend.repo.article.ArticleRepo
import net.ojm.web_backend.service.article.ArticleImageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

const val ARTICLE_IMAGE_FOLDER_PATH = "D:/Minor/Project/uploads/article/header_image/"

@Service
class ArticleImageServiceImpl (
    val articleImageRepo: ArticleImageRepo,
    val articleRepo: ArticleRepo
) : ArticleImageService {

    override fun uploadArticleImage(image: MultipartFile, articleId: UUID) {
        // Find the article first
        val article = articleRepo.findById(articleId).orElseThrow {
            IllegalArgumentException("Article with ID $articleId not found")
        }

        // Create the directory if it doesn't exist
        val directory = File(ARTICLE_IMAGE_FOLDER_PATH)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Get filename and set the path
        val fileName = image.originalFilename ?: throw IllegalArgumentException("Original filename is missing")
        val imagePath = ARTICLE_IMAGE_FOLDER_PATH + fileName

        // Create and save the entity
        val imageEntity = ArticleImageEntity(
            articleImageName = fileName,
            articleImageType = image.contentType ?: "application/octet-stream",
            articleImageFilePath = imagePath,
            article = article
        )

        // Save the entity to the database
        articleImageRepo.save(imageEntity)

        // Save the file to disk
        try {
            image.transferTo(File(imagePath))
        } catch (e: Exception) {
            // If file save fails, clean up the database entry
            articleImageRepo.delete(imageEntity)
            throw e
        }
    }

    override fun downloadArticleImage(articleId: UUID, url: String): ByteArray? {
        val imageData = articleImageRepo.findByArticle_ArticleIdAndArticleImageName(articleId, url)
            ?: throw FileNotFoundException("Image with name $url not found");

        val path = Paths.get(imageData.articleImageFilePath)
        if (!Files.exists(path)) {
            throw FileNotFoundException("File not found on disk at path: $path");
        }

        return Files.readAllBytes(path);
    }

}