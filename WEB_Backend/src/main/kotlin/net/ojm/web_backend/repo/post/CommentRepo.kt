package net.ojm.web_backend.repo.post

import net.ojm.web_backend.domain.entity.post.CommentEntity
import net.ojm.web_backend.domain.entity.post.PostEntity
import net.ojm.web_backend.domain.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepo : JpaRepository<CommentEntity, UUID> {

    fun findAllByPost(post: PostEntity): List<CommentEntity>

}