package ru.iscoach.persistence.repos

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.iscoach.persistence.entity.OrderInfoEntity

@Repository
interface OrderRepo : CrudRepository<OrderInfoEntity, Int> {
    fun findOrderById(id: Int): OrderInfoEntity?
}