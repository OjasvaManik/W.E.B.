package net.ojm.web_backend.service.article

import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface ArticleImageService {

    fun uploadArticleImage(image: MultipartFile, articleId: UUID)

    fun downloadArticleImage(articleId: UUID, url: String): ByteArray?;

}