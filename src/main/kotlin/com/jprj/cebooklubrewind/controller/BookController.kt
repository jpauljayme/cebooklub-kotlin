package com.jprj.cebooklubrewind.controller

import com.jprj.cebooklubrewind.service.BlogService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BookController(
    private val blogService: BlogService,
) {

    @GetMapping("/")
    fun index(model: Model): String {

        model.addAttribute("currentBook",  blogService.getCurrentRead())
        println(model)
        return "index"
    }
}