package utilits;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierApi {
    public static final String CREATE_COURIER = "/api/v1/courier";
    public static final String AUTHORIZATION_COURIER = "/api/v1/courier/login";
    public static final String DELETE_COURIER = "/api/v1/courier/{id}";

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        return given().header("Content-type", "application/json").and().body(courier).when().post(CREATE_COURIER);
    }

    @Step("Авторизация курьера")
    public Response authorizationCouriers(Courier courier) {
        return given().header("Content-type", "application/json").and().body(courier).when().post(AUTHORIZATION_COURIER);
    }

    @Step("Удаление курьера")
    public void deleteCourierRequest(int courierId) {
        given().pathParam("id", courierId).when().delete(DELETE_COURIER);
    }
}
