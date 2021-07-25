package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class JspMealController{
    @Autowired
    private MealService service;

    @GetMapping("/meals")
    public String getMeals(Model model) {
        int userId = SecurityUtil.authUserId();
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/meals/create")
    public String openFormCreate(Model model){
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        model.addAttribute("action", "Create meal");
        return "mealForm";
    }

    @GetMapping("/meals/meal/{id}")
    public String getMeal(@PathVariable Integer id, Model model){
        model.addAttribute("meal", service.get(id, SecurityUtil.authUserId()));
        model.addAttribute("action", "Edit meal");
        return "mealForm";
    }
    @GetMapping("/meals/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id, SecurityUtil.authUserId());
        return "redirect:/meals";
    }

    @PostMapping("/meals")
    public String create(@RequestParam(required = false) String id,
                          @RequestParam String dateTime,
                          @RequestParam String description,
                          @RequestParam Integer calories)
            throws UnsupportedEncodingException {

        Meal meal = new Meal(LocalDateTime.parse(dateTime), description, calories);
        Integer userId = SecurityUtil.authUserId();
        if (StringUtils.hasLength(id)) {
            assureIdConsistent(meal, Integer.parseInt(id));
            service.update(meal, userId);
        }
        else {
            checkNew(meal);
            service.create(meal, userId);
        }
        return "redirect:/meals";
    }
}
