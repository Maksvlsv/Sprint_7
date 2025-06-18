package ru.yandex.scooter.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.yandex.scooter.model.Courier;

import static io.restassured.RestAssured.given;

public class CourierClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public Response createCourier(Courier courier) {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    public Response loginCourier(Courier courier) {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    public Response deleteCourier(int courierId) {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }
}
