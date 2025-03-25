package com.jprj.cebooklubrewind.model

import java.time.LocalDate

data class Book(
    val metadata: BookMetadata,
    val content: String,
    val fileName: String,
    val fullImageUrl: String,
)

data class BookMetadata(
    val id: String = "",
    val title: String = "",
    val date: LocalDate,
    val imageName: String = "",
    val author: String = "",
    val pages: String = "",
    val firstPublishedYear: Int = 0,
    val tags: List<String> = listOf(),
)