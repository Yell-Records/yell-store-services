package com.yellrecords.services.category.mapper

import com.yellrecords.services.category.Category
import com.yellrecords.services.category.dto.CategoryDto

object CategoryMapper {
    fun toDto(category: Category) =
        CategoryDto(
            id = category.id!!,
            name = category.name,
            slug = category.slug,
            isActive = category.isActive,
            createdAt = category.createdAt,
        )
}
