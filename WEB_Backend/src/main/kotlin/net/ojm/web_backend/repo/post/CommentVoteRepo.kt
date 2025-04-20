package net.ojm.web_backend.repo.post

import net.ojm.web_backend.domain.entity.post.CommentEntity
import net.ojm.web_backend.domain.entity.post.CommentVoteEntity
import net.ojm.web_backend.domain.entity.post.VoteTypeEnum
import net.ojm.web_backend.domain.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentVoteRepo : JpaRepository<CommentVoteEntity, UUID> {

    fun findByCommentAndUser(comment: CommentEntity, user: UserEntity): CommentVoteEntity?

    fun countByCommentAndVoteType(comment: CommentEntity, type: VoteTypeEnum): Int

}