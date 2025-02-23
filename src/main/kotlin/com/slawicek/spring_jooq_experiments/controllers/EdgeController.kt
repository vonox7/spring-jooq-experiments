package com.slawicek.spring_jooq_experiments.controllers

import com.slawicek.spring_jooq_experiments.db.tables.references.EDGE
import org.jooq.impl.DefaultDSLContext
import org.springframework.web.bind.annotation.*

class EdgeDto(val from: Int, val to: Int)

class PutResponse(val status: Status) {
    enum class Status {
        EDGE_SUCCESSFULLY_INSERTED,
        EDGE_ALREADY_EXISTED
    }
}

class DeleteResponse(val status: Status) {
    enum class Status {
        EDGE_SUCCESSFULLY_DELETED,
        EDGE_DIDNT_EXIST
    }
}

@RestController
class EdgeController(private val dslContext: DefaultDSLContext) {

    @PutMapping("/")
    fun put(@RequestBody edgeDto: EdgeDto): PutResponse {
        val updateCount = dslContext
            .insertInto(EDGE)
            .set(EDGE.FROM_ID, edgeDto.from)
            .set(EDGE.TO_ID, edgeDto.to)
            .onDuplicateKeyIgnore() // Don't use try-catch for control flow, but check the updateCount
            .execute()

        return when (updateCount) {
            0 -> PutResponse(PutResponse.Status.EDGE_ALREADY_EXISTED)
            1 -> PutResponse(PutResponse.Status.EDGE_SUCCESSFULLY_INSERTED)
            else -> throw IllegalStateException("updateCount=$updateCount")
        }
    }

    @DeleteMapping("/")
    fun delete(@RequestBody edgeDto: EdgeDto): DeleteResponse {
        val deleteCount = dslContext
            .deleteFrom(EDGE)
            .where(EDGE.FROM_ID.eq(edgeDto.from), EDGE.TO_ID.eq(edgeDto.to))
            .execute()

        return when (deleteCount) {
            0 -> DeleteResponse(DeleteResponse.Status.EDGE_DIDNT_EXIST)
            1 -> DeleteResponse(DeleteResponse.Status.EDGE_SUCCESSFULLY_DELETED)
            else -> throw IllegalStateException("deleteCount=$deleteCount")
        }
    }
}