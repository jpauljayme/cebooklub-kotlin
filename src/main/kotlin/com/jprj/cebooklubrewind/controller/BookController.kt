package com.jprj.cebooklubrewind.controller

import com.jprj.cebooklubrewind.service.BlogService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxReswap
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRetarget
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxSwapType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@Controller
class BookController(
    private val blogService: BlogService,
) {

    @GetMapping("/")
    fun index(model: Model,
              @RequestHeader(value = "HX-Request", defaultValue = "false") isHxRequest: Boolean): String {

        model.addAttribute("currentBook",  blogService.getCurrentRead())
        return if (isHxRequest) "fragments/fragments :: currentRead" else "index"
//        return "fragments/fragments :: pastReads"
    }


    @HxRequest
    @HxReswap(value = HxSwapType.INNER_HTML)
    @HxRetarget(value = "#main-content")
    @GetMapping("/pastReads")
    fun pastReads(model: Model): String {
        model.addAttribute("books",  blogService.list())
        return "fragments/fragments :: pastReads"
    }

    @HxRequest
    @HxReswap(value = HxSwapType.INNER_HTML)
    @HxRetarget(value = "#main-content")
    @GetMapping("/aboutUs")
    fun aboutUs(model: Model): String {
        return "fragments/fragments :: aboutUs"
    }
}