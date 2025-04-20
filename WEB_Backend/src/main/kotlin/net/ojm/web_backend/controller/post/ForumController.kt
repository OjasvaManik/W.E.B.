package net.ojm.web_backend.controller.post

import jakarta.validation.Valid
import net.ojm.web_backend.domain.dto.post.request.CreateCommentRequest
import net.ojm.web_backend.domain.dto.post.request.CreatePostRequest
import net.ojm.web_backend.domain.dto.post.request.VoteCommentRequest
import net.ojm.web_backend.domain.dto.post.request.VotePostRequest
import net.ojm.web_backend.domain.dto.post.response.*
import net.ojm.web_backend.service.post.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/web/forum")
class ForumController(
    private val postService: PostService
) {

    @PostMapping(path = ["/create"])
    fun createPost(@Valid @RequestBody request: CreatePostRequest): ResponseEntity<String> {
        return postService.createPost(request)
    }

    @GetMapping
    fun getAllPosts(): List<GetAllPostsResponse> {
        return postService.getAllPosts()
    }

    @PostMapping(path = ["/post/comment"])
    fun createComment(@Valid @RequestBody request: CreateCommentRequest): ResponseEntity<String> {
        return postService.createComment(request);
    }

    @PutMapping(path = ["/post/upVote"])
    fun upVotePost(@RequestBody request: VotePostRequest): ResponseEntity<UpVotePostResponse> {
        return postService.upVotePost(request);
    }

    @PutMapping(path = ["/post/downVote"])
    fun downVotePost(@RequestBody request: VotePostRequest): ResponseEntity<DownVotePostResponse> {
        return postService.downVotePost(request);
    }

    @GetMapping(path = ["{postId}"])
    fun viewPost(@PathVariable("postId") postId: UUID): ViewPostResponse {
        return postService.viewPost(postId);
    }

    @PutMapping(path = ["/comment/upVote"])
    fun upVoteComment(@RequestBody request: VoteCommentRequest): ResponseEntity<UpVoteCommentResponse> {
        return postService.upVoteComment(request);
    }

    @PutMapping(path = ["/comment/downVote"])
    fun downVoteComment(@RequestBody request: VoteCommentRequest): ResponseEntity<DownVoteCommentResponse> {
        return postService.downVoteComment(request);
    }

//    @GetMapping("/post/{postId}/comments")
//    fun getAllCommentsByPostId(@PathVariable postId: UUID): List<GetAllCommentsResponse> {
//        return postService.getAllCommentsByPostId(postId)
//    }

}