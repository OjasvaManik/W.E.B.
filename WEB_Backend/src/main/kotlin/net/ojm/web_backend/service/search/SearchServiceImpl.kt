package net.ojm.web_backend.service.search

import net.ojm.web_backend.domain.dto.search.CombinedSearchResponse
import net.ojm.web_backend.domain.entity.article.ArticleEntity
import net.ojm.web_backend.domain.entity.post.PostEntity
import net.ojm.web_backend.repo.article.ArticleRepo
import net.ojm.web_backend.repo.post.PostRepo
import org.springframework.stereotype.Service

@Service
class SearchServiceImpl(
    private val articleRepository: ArticleRepo,
    private val postRepository: PostRepo
) : SearchService {

    override fun searchByTitle(searchTerm: String?): List<CombinedSearchResponse> {
        val result = mutableListOf<CombinedSearchResponse>()

        if (searchTerm.isNullOrBlank()) {
            // Get all articles and posts if no search term
            articleRepository.findAll().map {
                result.add(mapToArticleResponse(it))
            }

            postRepository.findAll().map {
                result.add(mapToPostResponse(it))
            }
        } else {
            // Search by title
            articleRepository.findByArticleTitleContainingIgnoreCase(searchTerm).map {
                result.add(mapToArticleResponse(it))
            }

            postRepository.findByPostTitleContainingIgnoreCase(searchTerm).map {
                result.add(mapToPostResponse(it))
            }
        }

        return result
    }

    private fun mapToArticleResponse(article: ArticleEntity): CombinedSearchResponse {
        return CombinedSearchResponse(
            id = article.articleId,
            title = article.articleTitle,
            content = article.articleContent.take(200) + if (article.articleContent.length > 200) "..." else "",
            userId = article.userId.userId,
            userName = article.userId.userName,
            type = "ARTICLE",
            source = article.articleSource
        )
    }

    private fun mapToPostResponse(post: PostEntity): CombinedSearchResponse {
        return CombinedSearchResponse(
            id = post.postId,
            title = post.postTitle,
            content = post.postContent.take(200) + if (post.postContent.length > 200) "..." else "",
            userId = post.userId.userId,
            userName = post.userId.userName,
            type = "POST",
            commentCount = post.comments.size
        )
    }
}