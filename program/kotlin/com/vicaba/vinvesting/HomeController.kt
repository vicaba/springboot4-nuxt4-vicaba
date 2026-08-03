package com.vicaba.vinvesting

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("title", "Vingvesting")
        model.addAttribute("tagline", "Smart Investment Tracking & Portfolio Insights")
        return "index"
    }
}
