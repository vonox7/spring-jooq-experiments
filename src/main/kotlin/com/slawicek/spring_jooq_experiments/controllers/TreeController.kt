package com.slawicek.spring_jooq_experiments.controllers

import com.slawicek.spring_jooq_experiments.db.tables.references.EDGE
import org.jooq.impl.DSL.*
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.SQLDataType.INTEGER
import org.springframework.web.bind.annotation.*

class TreeResponse(var rootNode: Node?) {
    class Node(val id: Int, val children: MutableList<Node>)
}

@RestController
class TreeController(private val dslContext: DefaultDSLContext) {

    @GetMapping("/{rootNode}")
    fun getTreeByNode(@PathVariable rootNode: Int): Any {
        val getTreeExpression = name("tree").`as`(
            select(EDGE.FROM_ID, EDGE.TO_ID)
                .from(EDGE)
                .where(EDGE.FROM_ID.eq(rootNode))
                .unionAll(
                    select(EDGE.FROM_ID, EDGE.TO_ID)
                        .from(table(name("tree")))
                        .join(EDGE)
                        .on(field(name("tree", EDGE.TO_ID.name), INTEGER).eq(EDGE.FROM_ID))
                )
        )

        val nodes = HashMap<Int, TreeResponse.Node>() // In-memory index to not traverse the tree when adding new nodes
        val response = TreeResponse(rootNode = null)

        dslContext
            .withRecursive(getTreeExpression)
            .selectFrom(getTreeExpression)
            .fetch()
            .forEach { edge ->
                val fromId: Int = edge[EDGE.FROM_ID]!!
                val toId: Int = edge[EDGE.TO_ID]!!

                val child = TreeResponse.Node(toId, mutableListOf()).also { nodes[it.id] = it }

                if (response.rootNode == null) {
                    response.rootNode = TreeResponse.Node(fromId, mutableListOf(child)).also { nodes[it.id] = it }
                } else {
                    // Parent must exist, as the sql query adds (recursively)
                    // first the queried edge and then unions all further edges.
                    val parentNode = nodes[fromId]!!
                    parentNode.children.add(child)
                }
            }

        return response
    }
}