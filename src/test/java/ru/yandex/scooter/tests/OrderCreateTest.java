package ru.yandex.scooter.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.scooter.client.OrderClient;
import ru.yandex.scooter.model.Order;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final List<String> color;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Object[][] getColors() {
        return new Object[][]{
                {Collections.singletonList("BLACK")},
                {Collections.singletonList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {Collections.emptyList()}
        };
    }

    @Test
    public void canCreateOrderWithVariousColors() {
        Order order = generateOrder(color);
        Response response = createOrder(order);
        validateResponseContainsTrack(response);
    }

    @Step("Создание заказа")
    private Response createOrder(Order order) {
        return new OrderClient().createOrder(order);
    }

    @Step("Генерация данных для заказа")
    private Order generateOrder(List<String> color) {
        return new Order(
                "Naruto", "Uzumaki", "Konoha, 142 apt.",
                "4", "+7 800 555 35 35", 5, "2025-06-18",
                "Rasengan delivery", color
        );
    }

    @Step("Проверка, что в ответе есть поле track")
    private void validateResponseContainsTrack(Response response) {
        response.then().statusCode(201).body("track", greaterThan(0));
    }
}