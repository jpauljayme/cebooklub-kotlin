package com.jprj.cebooklubrewind.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jprj.cebooklubrewind.model.Book
import com.jprj.cebooklubrewind.model.BookMetadata
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class BlogService(
    @Value("\${cloudfront.url}") val cloudfrontUrl: String,
) {

    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()
    private val yamlMapper = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())

    fun getCurrentRead(): Book? {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMM")
        val date = LocalDate.now().format(ofPattern)
        val markdownFilename = "$date.md"
        val book = getBook(markdownFilename)
        return book
    }

    fun list(): List<Book?> {
        val resource = ClassPathResource("markdown/")
        return resource.file.listFiles()?.map { file ->
            val content = Files.readString(file.toPath())
            parseMarkdown(content, file.nameWithoutExtension)
        } ?: emptyList()
    }

    fun getBook(filename: String): Book? {
        return try {
            val resource = ClassPathResource("markdown/$filename")

            if(!resource.exists()) {
                return null
            }
            val markdown = resource.inputStream.bufferedReader().use { it.readText() }
            parseMarkdown(markdown, filename)
        }catch (e: Exception) {
            println("Error loading file: ${e.message}")
            null
        }
    }

    private fun parseMarkdown(content: String, fileName: String): Book? {
        val (metadataText, markdownContent) = extractFrontMatter(content)
        val metadata: BookMetadata = yamlMapper.readValue(metadataText, BookMetadata::class.java) ?: return null
        return Book(
            metadata = metadata,
            content = markdownContent,
            fileName = fileName,
            fullImageUrl = "$cloudfrontUrl${metadata.imageName}"
        )
    }

    private fun extractFrontMatter(content: String): Pair<String, String> {
        val metadataRegex = Regex("---\\s*\\n(.*?)\\n---", RegexOption.DOT_MATCHES_ALL)
        val matchResult = metadataRegex.find(content)
        val metadata = matchResult?.groupValues?.get(1) ?: ""
        val markdownContent = content.replace(metadataRegex, "").trim()
        return Pair(metadata, markdownContent)
    }
}
