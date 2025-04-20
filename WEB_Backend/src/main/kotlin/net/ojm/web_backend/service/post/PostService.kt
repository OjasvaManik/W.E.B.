package net.ojm.web_backend.service.post

import net.ojm.web_backend.domain.dto.post.request.*
import net.ojm.web_backend.domain.dto.post.response.*
import org.springframework.http.ResponseEntity
import java.util.UUID

interface PostService {

    fun createPost(request: CreatePostRequest): ResponseEntity<String>

    fun getAllPosts(): List<GetAllPostsResponse>

    fun createComment(request: CreateCommentRequest): ResponseEntity<String>

    fun upVotePost(request: VotePostRequest): ResponseEntity<UpVotePostResponse>

    fun downVotePost(request: VotePostRequest): ResponseEntity<DownVotePostResponse>

    fun upVoteComment(request: VoteCommentRequest): ResponseEntity<UpVoteCommentResponse>

    fun downVoteComment(request: VoteCommentRequest): ResponseEntity<DownVoteCommentResponse>

//    fun viewComments(request: ViewCommentsRequest): List<ViewCommentsResponse>

//    fun getAllCommentsByPostId(postId: UUID): List<GetAllCommentsResponse>

    fun viewPost(request: UUID): ViewPostResponse

}