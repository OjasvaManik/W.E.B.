package net.ojm.web_backend.repo.article

import net.ojm.web_backend.domain.entity.article.StatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StatusRepo : JpaRepository<StatusEntity, UUID> {
}