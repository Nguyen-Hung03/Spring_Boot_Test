package com.example.testspringboot.controller;

import com.example.testspringboot.model.User;
import com.example.testspringboot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        return render(new User(), keyword, model);
    }

    @PostMapping("/users/save")
    public String save(@Valid @ModelAttribute("user") User user,
                       BindingResult bindingResult,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return render(user, keyword, model);
        }
        if (userService.isNameTaken(user.getName(), user.getId())) {
            String action = user.getId() == null ? "creating" : "updating";
            String verb = user.getId() == null ? "create" : "update";
            model.addAttribute("error",
                    "Error while " + action + " User: Unable to " + verb
                            + ". A User with name " + user.getName() + " already exist.");
            return render(user, keyword, model);
        }

        boolean creating = user.getId() == null;
        User saved = userService.save(user);
        if (creating) {
            redirectAttributes.addFlashAttribute("success", "User created successfully");
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("success", "User updated successfully");
        return "redirect:/users/edit/" + saved.getId();
    }

    @GetMapping("/users/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        return userService.findById(id)
                .map(user -> render(user, keyword, model))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "User not found");
                    return "redirect:/";
                });
    }

    @PostMapping("/users/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

    private String render(User user, String keyword, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("users", userService.search(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("isEdit", user.getId() != null);
        return "users";
    }
}
