package net.ojm.web_backend.repo.article

import net.ojm.web_backend.domain.entity.article.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CategoryRepo : JpaRepository<CategoryEntity, UUID> {

    fun findAllByCategoryIdIn(categoryIds: List<UUID>): List<CategoryEntity>

}