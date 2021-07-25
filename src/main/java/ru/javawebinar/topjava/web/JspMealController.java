package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
@RequestMapping("/meals")
public class JspMealController{
    @Autowired
    private MealService service;

    @GetMapping
    public String getMeals(Model model) {
        int userId = SecurityUtil.authUserId();
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/create")
    public String openFormCreate(Model model){
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        model.addAttribute("action", "Add meal");
        return "mealForm";
    }

    @GetMapping("/meal/{id}")
    public String getMeal(@PathVariable Integer id, Model model){
        model.addAttribute("meal", service.get(id, SecurityUtil.authUserId()));
        model.addAttribute("action", "Edit meal");
        return "mealForm";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id, SecurityUtil.authUserId());
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam String startDate,
                         @RequestParam String endDate,
                         @RequestParam String startTime,
                         @RequestParam String endTime,
                         Model model) {
        model.addAttribute("meals",
                getBetween(parseLocalDate(startDate),
                parseLocalTime(startTime),
                parseLocalDate(endDate),
                parseLocalTime(endTime))
        );
        return "meals";
    }

    @PostMapping
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

    public List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        int userId = SecurityUtil.authUserId();
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        return MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }
}
