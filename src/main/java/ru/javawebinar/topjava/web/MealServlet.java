package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.allMealByStreams;
import static ru.javawebinar.topjava.util.MockInitMealsList.initMealList;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    public static final int CALORIES_PER_DAY = 2000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");
        List<Meal> meals = initMealList();
        List<MealTo> mealToList = allMealByStreams(meals, CALORIES_PER_DAY);

        request.setAttribute("meals", mealToList);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
        //response.sendRedirect("meals.jsp");
    }
}
