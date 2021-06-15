package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public Meal save(Meal meal) {
        MealDataBase dataBase = new MealDataBase(meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), dataBase);
            return meal;
        }
        // handle case: update, but not present in storage
        return Objects.requireNonNull(repository.computeIfPresent(meal.getId(), (id, old) -> dataBase)).meal;
    }

    @Override
    public boolean delete(int id) {
        if (isBelongToUserId(repository.get(id).FOREIGN_KEY ,SecurityUtil.authUserId()))
            return repository.remove(id) != null;
        return false;
    }

    @Override
    public Meal get(int id) {
        return isBelongToUserId(repository.get(id).FOREIGN_KEY ,SecurityUtil.authUserId())?repository.get(id).meal :null;
    }

    @Override
    public Collection<Meal> getAll() {
        return repository.values().stream()
                .filter(mealDataBase -> isBelongToUserId(mealDataBase.FOREIGN_KEY ,SecurityUtil.authUserId()))
                //.sorted(Comparator.comparing(mealDataBase -> mealDataBase.meal,(o, t1) -> t1.getDateTime().compareTo(o.getDateTime())))
                .map(mealDataBase -> mealDataBase.meal)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean isBelongToUserId(int foreignKey, int userId){
        return foreignKey == userId;
    }
    private static class MealDataBase {
        private final int FOREIGN_KEY = SecurityUtil.authUserId();
        private final Meal meal;

        private MealDataBase(Meal meal) {
            this.meal = meal;
        }
    }
}