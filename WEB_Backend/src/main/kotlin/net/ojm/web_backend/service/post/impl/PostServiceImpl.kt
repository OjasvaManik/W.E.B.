package net.ojm.web_backend.service.post.impl

import jakarta.persistence.EntityNotFoundException
import net.ojm.web_backend.domain.dto.post.request.*
import net.ojm.web_backend.domain.dto.post.response.*
import net.ojm.web_backend.domain.entity.post.*
import net.ojm.web_backend.repo.post.CommentRepo
import net.ojm.web_backend.repo.post.CommentVoteRepo
import net.ojm.web_backend.repo.post.PostRepo
import net.ojm.web_backend.repo.post.PostVoteRepo
import net.ojm.web_backend.repo.user.UserRepo
import net.ojm.web_backend.service.post.PostService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PostServiceImpl(
    val postRepo: PostRepo,
    val commentRepo: CommentRepo,
    val postVoteRepo: PostVoteRepo,
    val commentVoteRepo: CommentVoteRepo,
    val userRepo: UserRepo
) : PostService {

    @Transactional
    override fun createPost(request: CreatePostRequest): ResponseEntity<String> {
        val user = userRepo.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val post = PostEntity(
            postTitle = request.postTitle,
            postContent = request.postContent,
            userId = user
        )
        val savedPost = postRepo.save(post)

        return ResponseEntity.ok("Created post with ID: ${savedPost.postId}")
    }

    override fun getAllPosts(): List<GetAllPostsResponse> {
        val posts = postRepo.findAll()

        return posts.map { post ->
            GetAllPostsResponse(
                postId = post.postId,
                postTitle = post.postTitle,
                postContent = post.postContent,
                userId = post.userId.userId,
                userName = post.userId.userName,
                commentCount = post.comments.size,
                upVotes = post.vote.count { it.voteType == VoteTypeEnum.UPVOTE },
                downVotes = post.vote.count { it.voteType == VoteTypeEnum.DOWNVOTE }
            )
        }
    }

    @Transactional
    override fun createComment(request: CreateCommentRequest): ResponseEntity<String> {
        val post = postRepo.findById(request.postId)
            .orElseThrow { EntityNotFoundException("Post Not Found") }

        val user = userRepo.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val comment = CommentEntity(
            commentContent = request.commentContent,
            post = post,
            user = user
        )

        commentRepo.save(comment)
        return ResponseEntity.ok("Created")
    }

    @Transactional
    override fun upVotePost(request: VotePostRequest): ResponseEntity<UpVotePostResponse> {
        val post = postRepo.findById(request.postId)
            .orElseThrow { EntityNotFoundException("Post Not Found") }

        val user = userRepo.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val existingVote = postVoteRepo.findByPostAndUser(post, user)

        return when {
            existingVote?.voteType == VoteTypeEnum.UPVOTE -> {
                postVoteRepo.delete(existingVote)
                val currentUpvotes = postVoteRepo.countByPostAndVoteType(post, VoteTypeEnum.UPVOTE)
                ResponseEntity.ok(UpVotePostResponse(post.postId, currentUpvotes))
            }
            existingVote != null -> {
                existingVote.voteType = VoteTypeEnum.UPVOTE
                postVoteRepo.save(existingVote)
                val currentUpvotes = postVoteRepo.countByPostAndVoteType(post, VoteTypeEnum.UPVOTE)
                ResponseEntity.ok(UpVotePostResponse(post.postId, currentUpvotes))
            }
            else -> {
                postVoteRepo.save(
                    PostVoteEntity(
                    post = post,
                    user = user,
                    voteType = VoteTypeEnum.UPVOTE
                )
                )
                val currentUpvotes = postVoteRepo.countByPostAndVoteType(post, VoteTypeEnum.UPVOTE)
                ResponseEntity.ok(UpVotePostResponse(post.postId, currentUpvotes))
            }
        }
    }

    @Transactional
    override fun downVotePost(request: VotePostRequest): ResponseEntity<DownVotePostResponse> {
        val post = postRepo.findById(request.postId)
            .orElseThrow { EntityNotFoundException("Post Not Found") }

        val user = userRepo.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val existingVote = postVoteRepo.findByPostAndUser(post, user)

        return when {
            existingVote?.voteType == VoteTypeEnum.DOWNVOTE -> {
                postVoteRepo.delete(existingVote)
                val currentDownVotes = postVoteRepo.countByPostAndVoteType(post, VoteTypeEnum.DOWNVOTE)
                ResponseEntity.ok(DownVotePostResponse(post.postId, currentDownVotes))
            }
            existingVote != null -> {
                existingVote.voteType = VoteTypeEnum.DOWNVOTE
                postVoteRepo.save(existingVote)
                val currentDownVotes = postVoteRepo.countByPostAndVoteType(post, VoteTypeEnum.DOWNVOTE)
                ResponseEntity.ok(DownVotePostResponse(post.postId, currentDownVotes))
            }
            else -> {
                postVoteRepo.save(
                    PostVoteEntity(
                        post = post,
                        user = user,
                        voteType = VoteTypeEnum.DOWNVOTE
                    )
                )
                val currentDownVotes = postVoteRepo.countByPostAndVoteType(post, VoteTypeEnum.DOWNVOTE)
                ResponseEntity.ok(DownVotePostResponse(post.postId, currentDownVotes))
            }
        }
    }

    @Transactional
    override fun upVoteComment(request: VoteCommentRequest): ResponseEntity<UpVoteCommentResponse> {
        val comment = commentRepo.findById(request.commentId)
            .orElseThrow { EntityNotFoundException("Comment Not Found") }

        val user = userRepo.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val existingVote = commentVoteRepo.findByCommentAndUser(comment, user)

        return when {
            existingVote?.voteType == VoteTypeEnum.UPVOTE -> {
                commentVoteRepo.delete(existingVote)
                val currentUpvotes = commentVoteRepo.countByCommentAndVoteType(comment, VoteTypeEnum.UPVOTE)
                ResponseEntity.ok(UpVoteCommentResponse(comment.commentId, currentUpvotes))
            }
            existingVote != null -> {
                existingVote.voteType = VoteTypeEnum.UPVOTE
                commentVoteRepo.save(existingVote)
                val currentUpvotes = commentVoteRepo.countByCommentAndVoteType(comment, VoteTypeEnum.UPVOTE)
                ResponseEntity.ok(UpVoteCommentResponse(comment.commentId, currentUpvotes))
            }
            else -> {
                commentVoteRepo.save(
                    CommentVoteEntity(
                        comment = comment,
                        user = user,
                        voteType = VoteTypeEnum.UPVOTE
                    )
                )
                val currentUpvotes = commentVoteRepo.countByCommentAndVoteType(comment, VoteTypeEnum.UPVOTE)
                ResponseEntity.ok(UpVoteCommentResponse(comment.commentId, currentUpvotes))
            }
        }
    }

    @Transactional
    override fun downVoteComment(request: VoteCommentRequest): ResponseEntity<DownVoteCommentResponse> {
        val comment = commentRepo.findById(request.commentId)
            .orElseThrow { EntityNotFoundException("Comment Not Found") }

        val user = userRepo.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val existingVote = commentVoteRepo.findByCommentAndUser(comment, user)

        return when {
            existingVote?.voteType == VoteTypeEnum.DOWNVOTE -> {
                commentVoteRepo.delete(existingVote)
                val currentDownVotes = commentVoteRepo.countByCommentAndVoteType(comment, VoteTypeEnum.DOWNVOTE)
                ResponseEntity.ok(DownVoteCommentResponse(comment.commentId, currentDownVotes))
            }
            existingVote != null -> {
                existingVote.voteType = VoteTypeEnum.DOWNVOTE
                commentVoteRepo.save(existingVote)
                val currentDownVotes = commentVoteRepo.countByCommentAndVoteType(comment, VoteTypeEnum.DOWNVOTE)
                ResponseEntity.ok(DownVoteCommentResponse(comment.commentId, currentDownVotes))
            }
            else -> {
                commentVoteRepo.save(
                    CommentVoteEntity(
                        comment = comment,
                        user = user,
                        voteType = VoteTypeEnum.DOWNVOTE
                    )
                )
                val currentDownVotes = commentVoteRepo.countByCommentAndVoteType(comment, VoteTypeEnum.DOWNVOTE)
                ResponseEntity.ok(DownVoteCommentResponse(comment.commentId, currentDownVotes))
            }
        }
    }

//    @Transactional(readOnly = true)
//    override fun viewComments(request: ViewCommentsRequest): List<ViewCommentsResponse> {
//        val post = postRepo.findById(request.post.postId)
//            .orElseThrow { EntityNotFoundException("Post Not Found") }
//
//        return commentRepo.findAllByPost(post).map {
//            ViewCommentsResponse(
//                commentId = it.commentId,
//                userId = it.user.userId,
//                userName = it.user.userName,
//                commentContent = it.commentContent,
//                upVotes = it.vote.count { vote -> vote.voteType == VoteTypeEnum.UPVOTE },
//                downVotes = it.vote.count { vote -> vote.voteType == VoteTypeEnum.DOWNVOTE }
//            )
//        }
//    }

    @Transactional(readOnly = true)
    override fun viewPost(request: UUID): ViewPostResponse {
        val post = postRepo.findById(request)
            .orElseThrow { EntityNotFoundException("Post Not Found") }

        val user = userRepo.findById(post.userId.userId)
            .orElseThrow { EntityNotFoundException("User Not Found") }

        val comments = commentRepo.findAllByPost(post)
            .map {
                ViewCommentsResponse(
                    commentId = it.commentId,
                    userId = it.user.userId,
                    userName = it.user.userName,
                    commentContent = it.commentContent,
                    upVotes = it.vote.count { vote -> vote.voteType == VoteTypeEnum.UPVOTE },
                    downVotes = it.vote.count { vote -> vote.voteType == VoteTypeEnum.DOWNVOTE }
                )
            }

        return ViewPostResponse(
            postId = post.postId,
            userId = user.userId,
            userName = user.userName,
            postTitle = post.postTitle,
            postContent = post.postContent,
            comments = comments,
            upVotes = post.vote.count { vote -> vote.voteType == VoteTypeEnum.UPVOTE },
            downVotes = post.vote.count { vote -> vote.voteType == VoteTypeEnum.DOWNVOTE }
        )
    }

}