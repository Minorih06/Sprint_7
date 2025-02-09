import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static utilits.constants.Constants.*;

public class CourierCreateTest {
    private boolean shouldDeleteCourier = true;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Description("При заполнении всех полей валидными значениями получаем 201")
    public void courierCanBeCreatedTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER, FIRST_NAME_COURIER);
        Response response = sendPostRequest(courier, CREATE_COURIER);
        checkedStatusResponse(response, 201);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void impossibleToCreateTwoIdenticalCouriersTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER, FIRST_NAME_COURIER);
        sendPostRequest(courier, CREATE_COURIER);
        Response response = sendPostRequest(courier, CREATE_COURIER);
        checkedStatusResponse(response, 409);
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля;")
    @Description("Проверяем, что без поля firstName в код ответа будет 201")
    public void createCourierWithoutPasswordFieldTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        Response response = sendPostRequest(courier, CREATE_COURIER);
        checkedStatusResponse(response, 201);
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    public void successfulRequestReturnsValidBodyTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER, FIRST_NAME_COURIER);
        Response response = sendPostRequest(courier, CREATE_COURIER);
        checkedBodyResponse(response, "{\"ok\":true}");
    }

    @Test
    @DisplayName("Если одного из полей нет, запрос возвращает ошибку 400")
    @Description("Проверяем, что при отправке запроса без поля password получаем 400 Bad Request")
    public void ifOneOfTheFieldsIsMissingReturnsErrorTest() {
        shouldDeleteCourier = false;
        String json = String.format("{\"login\": \"%s\", \"firstName\":\"%s\"}", LOGIN_COURIER, FIRST_NAME_COURIER);
        Response response = given().header("Content-type", "application/json").and().body(json).when().post(CREATE_COURIER);
        checkedStatusResponse(response, 400);
    }

    @Test
    @DisplayName("Если создать пользователя с логином, который уже есть, возвращается ошибка.")
    @Description("Текст ошибки message: Этот логин уже используется")
    public void errorIsReturnedIfCreateUserWithALoginThatAlreadyExistTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER, FIRST_NAME_COURIER);
        sendPostRequest(courier, CREATE_COURIER);
        Response response = sendPostRequest(courier, CREATE_COURIER);
        checkedBodyResponse(response, "{\"message\":\"Этот логин уже используется\"}");
    }

    @Step("Отправка POST запроса")
    public Response sendPostRequest(Courier courier, String handle) {
        return given().header("Content-type", "application/json").and().body(courier).when().post(handle);
    }

    @Step("Проверка статуса ответа")
    public void checkedStatusResponse(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка тела ответа")
    public void checkedBodyResponse(Response response, String responseBody) {
        response.then().body(equalTo(responseBody));
    }

    @Step("Удаление курьера")
    public void deleteCourierRequest(int courierId) {
        given().pathParam("id", courierId).when().delete(DELETE_COURIER);
    }

    @AfterEach
    public void deleteCourier() {
        if (shouldDeleteCourier) {
            Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
            Response response = sendPostRequest(courier, AUTHORIZATION_COURIER);
            int courierId = response.jsonPath().getInt("id");
            deleteCourierRequest(courierId);
        }
    }
}
