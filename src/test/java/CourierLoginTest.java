import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static utilits.constants.Constants.*;

public class CourierLoginTest {
    private boolean shouldDeleteCourier = true;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Курьер может авторизоваться / Для авторизации нужно передать все обязательные поля")
    public void courierCanLogInTest(){
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        createCourier(courier);
        Response response = authorizationCouriers(courier);
        checkedStatusResponse(response, 200);
    }

    @Test
    @DisplayName("Система вернёт ошибку 404, если неправильно указать логин или пароль")
    @Description("В запросе на авторизацию указываем некорректный password")
    public void getErrorWithAnIncorrectPasswordTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        createCourier(courier);
        Courier courierIncorrectPassword = new Courier(LOGIN_COURIER, "4321");
        Response response = authorizationCouriers(courierIncorrectPassword);
        checkedStatusResponse(response, 404);
    }

    @Test
    @DisplayName("Если какого-то поля нет, запрос возвращает ошибку 400")
    @Description("Отправляем запрос без поля password")
    public void ifOneOfTheFieldsIsMissingReturnsErrorTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        createCourier(courier);
        Courier courierWithoutPassword = new Courier(LOGIN_COURIER);
        Response response = authorizationCouriers(courierWithoutPassword);
        checkedStatusResponse(response, 400);
    }

    @Test
    @DisplayName("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку 404")
    public void authorizationUnregisteredUserTest() {
        shouldDeleteCourier = false;
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        Response response = authorizationCouriers(courier);
        checkedStatusResponse(response, 404);
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    public void successfulRequestReturnsIdTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        createCourier(courier);
        Response response = authorizationCouriers(courier);
        checkedBodyResponse(response);
    }

    @Step("Создание курьера")
    public void createCourier(Courier courier) {
        given().header("Content-type", "application/json").and().body(courier).when().post(CREATE_COURIER);
    }

    @Step("Авторизация курьера")
    public Response authorizationCouriers(Courier courier) {
        return given().header("Content-type", "application/json").and().body(courier).when().post(AUTHORIZATION_COURIER);
    }

    @Step("Проверка статуса ответа")
    public void checkedStatusResponse(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка что id присутствует и не пустой")
    public void checkedBodyResponse (Response response) {
        response.then().assertThat().body("id", notNullValue());
    }

    @Step("Удаление курьера")
    public void deleteCourierRequest(int courierId) {
        given().pathParam("id", courierId).when().delete(DELETE_COURIER);
    }

    @AfterEach
    public void deleteCourier() {
        if (shouldDeleteCourier) {
            Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
            Response response = authorizationCouriers(courier);
            int courierId = response.jsonPath().getInt("id");
            deleteCourierRequest(courierId);
        }
    }

}
