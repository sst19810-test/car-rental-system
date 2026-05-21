package com.carrentalsystem.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index"; // ✅ Landing page
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403"; // ✅ Custom forbidden page
    }
    @GetMapping("/404")
    public String noContentFound() {
        return "error/404"; // ✅ Custom forbidden page
    }
    @GetMapping("/500")
    public String internalServerError() {
        return "error/500"; // ✅ Custom forbidden page
    }
    @GetMapping("/error")
    public String error() {
        return "error/error"; // ✅ General error fallback
    }
}
