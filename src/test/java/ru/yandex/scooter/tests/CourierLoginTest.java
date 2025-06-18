package ru.yandex.scooter.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.*;
import ru.yandex.scooter.client.CourierClient;
import ru.yandex.scooter.model.Courier;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CourierLoginTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = generateUniqueCourier();
        courierClient.createCourier(courier);
        courierId = loginAndGetCourierId(courier); // нужно, чтобы удалить в tearDown
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    public void courierCanLoginSuccessfully() {
        Response response = loginCourier(courier);
        validateLoginSuccessful(response);
    }

    @Test
    public void loginFailsWithWrongPassword() {
        Courier wrongPassword = new Courier(courier.getLogin(), "wrong123", null);
        Response response = loginCourier(wrongPassword);
        validateLoginError(response);
    }

    @Test
    public void loginFailsWithWrongLogin() {
        Courier wrongLogin = new Courier("invalid_login", courier.getPassword(), null);
        Response response = loginCourier(wrongLogin);
        validateLoginError(response);
    }

    @Test
    public void loginFailsWithMissingLogin() {
        Courier noLogin = new Courier(null, courier.getPassword(), null);
        Response response = loginCourier(noLogin);
        validateMissingFieldsError(response);
    }

    @Test
    public void loginFailsWithMissingPassword() {
        Courier noPassword = new Courier(courier.getLogin(), "", null);
        Response response = loginCourier(noPassword);
        validateMissingFieldsError(response);
    }

    @Test
    public void loginFailsWithNonexistentUser() {
        Courier fake = new Courier("notExist" + System.currentTimeMillis(), "1234", null);
        Response response = loginCourier(fake);
        validateLoginError(response);
    }

    // ===== Allure Steps =====

    @Step("Генерация уникального курьера")
    private Courier generateUniqueCourier() {
        return new Courier("ninja" + System.currentTimeMillis(), "1234", "saske");
    }

    @Step("Логин курьера")
    private Response loginCourier(Courier courier) {
        return courierClient.loginCourier(courier);
    }

    @Step("Проверка успешной авторизации")
    private void validateLoginSuccessful(Response response) {
        response.then().statusCode(200).body("id", notNullValue());
    }

    @Step("Проверка ошибки авторизации (неверный логин или пароль)")
    private void validateLoginError(Response response) {
        response.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Проверка ошибки: недостаточно данных")
    private void validateMissingFieldsError(Response response) {
        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Авторизация для получения ID")
    private Integer loginAndGetCourierId(Courier courier) {
        return courierClient.loginCourier(courier)
                .then()
                .extract()
                .path("id");
    }
}