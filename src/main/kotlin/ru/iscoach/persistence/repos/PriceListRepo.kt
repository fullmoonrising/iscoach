package ru.iscoach.persistence.repos

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.iscoach.persistence.entity.PriceListEntity
import ru.iscoach.service.model.Product

@Repository
interface PriceListRepo : CrudRepository<PriceListEntity, Product> {
    fun findItemById(id: Product): PriceListEntity?

    fun findAllByOrderByOrder(): List<PriceListEntity>
}