package ru.lab.inventory.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.lab.inventory.exception.ItemNotFoundException;

@ControllerAdvice(assignableTypes = InventoryWebController.class)
public class WebExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public String onNotFound(ItemNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}