<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="ru">
    <head>
        <title>Meals</title>
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h2>Meals</h2>
        <table border="1" cellspacing="0" cellpadding="2">
            <tr>
                <td>Date</td>
                <td>Description</td>
                <td>Calories</td>
                <td></td>
                <td></td>
            </tr>

            <c:forEach items="${meals}" var="meal">
                <tr style="color:${meal.excess?"Red":"Green"}">
                    <td>${meal.date} ${meal.time}</td>
                    <td>${meal.description}</td>
                    <td>${meal.calories}</td>
                    <td><a href="/update">Update</a></td>
                    <td><a href="/delete">Delete</a></td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>