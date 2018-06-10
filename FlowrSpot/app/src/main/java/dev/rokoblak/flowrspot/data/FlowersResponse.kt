package dev.rokoblak.flowrspot.data


data class FlowersResponse(val flowers: List<Flower>, val meta: Meta)

data class Meta(val pagination: Pagination)

data class Pagination(
        val current_page: Int?,
        val prev_page: Int?,
        val next_page: Int?,
        val total_pages: Int?
)