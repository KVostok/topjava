package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mapMeals = new HashMap<>();
        meals.forEach(
                (e) -> mapMeals.merge(e.getDateTime().toLocalDate(), e.getCalories(), Integer::sum)
        );

        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        meals.forEach((e) -> {
            if (TimeUtil.isBetweenHalfOpen(e.getDateTime().toLocalTime(), startTime, endTime))
                mealsWithExcess.add(new UserMealWithExcess(e.getDateTime(),
                        e.getDescription(),
                        e.getCalories(),
                        mapMeals.get(e.getDateTime().toLocalDate()) > caloriesPerDay));
        });
        return mealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> map = meals.stream().collect(Collector.of(
                HashMap::new,
                (p, i) -> p.merge(i.getDateTime().toLocalDate(), i.getCalories(), Integer::sum),
                (p1, p2) -> {
                    p1.putAll(p2);
                    return p1;
                }));

        return meals
                .stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .collect(
                        ArrayList::new,
                        (list, item) -> list.add(new UserMealWithExcess(item.getDateTime(), item.getDescription(), item.getCalories(),
                                map.get(item.getDateTime().toLocalDate()) > caloriesPerDay)),
                        ArrayList::addAll);
    }
}
