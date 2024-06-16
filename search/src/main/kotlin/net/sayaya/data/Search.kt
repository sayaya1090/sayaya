package net.sayaya.data

import org.springframework.web.reactive.function.server.ServerRequest

@JvmRecord
data class Search (
    val page: Int?,
    val limit: Int?,
    val sortBy: String?,
    val asc: Boolean?,
    val filters: MutableList<Pair<String, String>>
) {
    companion object {
        fun ServerRequest.param(): Search {
            val map = this.queryParams()
            val filters = map.filterKeys { key -> key !in setOf("page", "limit", "sort_by", "asc") }
                .flatMap { (key, value) -> value.map { key to it } }
                .toMutableList()
            return Search(
                page = map.getFirst("page")?.toIntOrNull(),
                limit = map.getFirst("limit")?.toIntOrNull(),
                sortBy = map.getFirst("sort_by"),
                asc = map.getFirst("asc")?.toBoolean(),
                filters = filters
            )
        }
    }
}