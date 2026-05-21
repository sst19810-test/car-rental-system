package com.carrentalsystem.app.exception;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        ex.printStackTrace(); // Log the exception for debugging
        System.out.println(ex.getMessage());
        model.addAttribute("errmsg",ex.getMessage());
        return "error/error" ;
    }
}