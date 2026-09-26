package ru.lab.inventory.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.lab.inventory.dto.InventoryItemRequest;
import ru.lab.inventory.exception.DuplicateInventoryNumberException;
import ru.lab.inventory.service.InventoryService;

import jakarta.validation.Valid;

@Controller
public class InventoryWebController {

    private final InventoryService service;

    public InventoryWebController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/inventory";
    }

    /** Список + счётчик «сколько неисправно» + форма добавления. */
    @GetMapping("/inventory")
    public String list(Model model) {
        fillModel(model, new InventoryItemRequest());
        return "inventory/list";
    }

    /** Binding + Validation: @Valid + BindingResult. */
    @PostMapping("/inventory")
    public String add(@Valid @ModelAttribute("form") InventoryItemRequest form,
                      BindingResult binding, Model model) {
        if (!binding.hasErrors()) {
            try {
                service.create(form);
                return "redirect:/inventory";
            } catch (DuplicateInventoryNumberException e) {
                binding.rejectValue("inventoryNumber", "error.duplicate",
                        new Object[]{form.getInventoryNumber()}, null);
            }
        }
        fillModel(model, form);
        return "inventory/list";
    }

    @PostMapping("/inventory/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/inventory";
    }

    private void fillModel(Model model, InventoryItemRequest form) {
        model.addAttribute("items", service.findAll());
        model.addAttribute("faultyCount", service.countFaulty());
        model.addAttribute("form", form);
    }
}