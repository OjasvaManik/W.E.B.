package net.ojm.web_backend.repo.post

import net.ojm.web_backend.domain.entity.post.PostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepo : JpaRepository<PostEntity, UUID> {

    fun findByPostTitleContainingIgnoreCase(title: String): List<PostEntity>

}