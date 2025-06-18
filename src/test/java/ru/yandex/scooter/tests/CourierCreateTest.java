package ru.yandex.scooter.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.*;
import ru.yandex.scooter.client.CourierClient;
import ru.yandex.scooter.model.Courier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CourierCreateTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = generateUniqueCourier();
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    public void courierCanBeCreated() {
        Response response = sendCreateRequest(courier);
        validateCreatedSuccessfully(response);
        courierId = loginAndGetCourierId(courier);
        assertNotNull("Courier ID должен быть не null", courierId);
    }

    @Test
    public void cannotCreateDuplicateCourier() {
        courierClient.createCourier(courier);
        Response response = sendCreateRequest(courier);
        validateDuplicateLoginError(response);
        courierId = loginAndGetCourierId(courier);
    }

    @Test
    public void cannotCreateWithoutLogin() {
        Courier noLogin = new Courier(null, courier.getPassword(), courier.getFirstName());
        Response response = sendCreateRequest(noLogin);
        validateMissingFieldsError(response);
    }

    @Test
    public void cannotCreateWithoutPassword() {
        Courier noPassword = new Courier(courier.getLogin(), null, courier.getFirstName());
        Response response = sendCreateRequest(noPassword);
        validateMissingFieldsError(response);
    }

    @Test
    public void cannotCreateWithoutLoginAndPassword() {
        Courier empty = new Courier(null, null, courier.getFirstName());
        Response response = sendCreateRequest(empty);
        validateMissingFieldsError(response);
    }

    // ========== Allure шаги ==========

    @Step("Генерация уникального курьера")
    private Courier generateUniqueCourier() {
        return new Courier("ninja" + System.currentTimeMillis(), "1234", "Naruto");
    }

    @Step("Отправка запроса на создание курьера")
    private Response sendCreateRequest(Courier courier) {
        return courierClient.createCourier(courier);
    }

    @Step("Проверка успешного создания курьера")
    private void validateCreatedSuccessfully(Response response) {
        response.then().statusCode(201).body("ok", equalTo(true));
    }

    @Step("Проверка ошибки: логин уже используется")
    private void validateDuplicateLoginError(Response response) {
        response.then().statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Проверка ошибки: недостаточно данных")
    private void validateMissingFieldsError(Response response) {
        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Авторизация и получение ID курьера")
    private Integer loginAndGetCourierId(Courier courier) {
        return courierClient.loginCourier(courier)
                .then()
                .extract()
                .path("id");
    }
}
