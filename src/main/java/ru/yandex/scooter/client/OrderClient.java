package ru.yandex.scooter.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.yandex.scooter.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public Response createOrder(Order order) {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }
}