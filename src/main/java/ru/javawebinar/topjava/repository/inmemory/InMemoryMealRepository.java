package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, MealDataBase> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    private void save(Meal meal) {
        save(meal, SecurityUtil.authUserId());
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MealDataBase dataBase = new MealDataBase(meal, userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), dataBase);
            return meal;
        }
        // handle case: update, but not present in storage
        return repository.getOrDefault(repository.computeIfPresent(meal.getId(), (id, old) -> dataBase),dataBase).meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        if (isBelongToUserId(repository.get(id).foreignKey ,userId))
            return repository.remove(id) != null;
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        return isBelongToUserId(repository.get(id).foreignKey ,userId)?repository.get(id).meal :null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(mealDataBase -> isBelongToUserId(mealDataBase.foreignKey ,userId))
                //.sorted(Comparator.comparing(mealDataBase -> mealDataBase.meal,(o, t1) -> t1.getDateTime().compareTo(o.getDateTime())))
                .map(mealDataBase -> mealDataBase.meal)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean isBelongToUserId(int foreignKey, int userId){
        return foreignKey == userId;
    }

    private static class MealDataBase {
        private final int foreignKey;
        private final Meal meal;

        private MealDataBase(Meal meal, int userId) {
            this.meal = meal;
            this.foreignKey = userId;
        }
    }
}