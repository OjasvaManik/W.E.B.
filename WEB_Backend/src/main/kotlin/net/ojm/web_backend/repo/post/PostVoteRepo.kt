package net.ojm.web_backend.repo.post

import net.ojm.web_backend.domain.entity.post.PostEntity
import net.ojm.web_backend.domain.entity.post.PostVoteEntity
import net.ojm.web_backend.domain.entity.post.VoteTypeEnum
import net.ojm.web_backend.domain.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostVoteRepo : JpaRepository<PostVoteEntity, UUID> {

    fun findByPostAndUser(post: PostEntity, user: UserEntity): PostVoteEntity?

    fun countByPostAndVoteType(post: PostEntity, type: VoteTypeEnum): Int

}