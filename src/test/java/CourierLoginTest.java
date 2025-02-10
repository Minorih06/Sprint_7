import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilits.Courier;
import utilits.CourierApi;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static utilits.CourierApi.AUTHORIZATION_COURIER;
import static utilits.constants.Constants.*;
import static org.apache.http.HttpStatus.*;

public class CourierLoginTest {
    private boolean shouldDeleteCourier = true;
    private int courierId;

    Faker faker = new Faker();
    private final String LOGIN_COURIER = faker.name().username();
    private final String PASSWORD_COURIER = faker.internet().password();

    CourierApi courierApi = new CourierApi();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Курьер может авторизоваться / Для авторизации нужно передать все обязательные поля")
    public void courierCanLogInTest(){
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        courierApi.createCourier(courier);
        Response response = courierApi.authorizationCouriers(courier);
        checkedStatusResponse(response, SC_OK);
        courierId = response.jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Система вернёт ошибку 404, если неправильно указать логин или пароль")
    @Description("В запросе на авторизацию указываем некорректный password")
    public void getErrorWithAnIncorrectPasswordTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        courierApi.createCourier(courier);
        Courier courierIncorrectPassword = new Courier(LOGIN_COURIER, "1234");
        Response response = courierApi.authorizationCouriers(courierIncorrectPassword);
        checkedStatusResponse(response, SC_NOT_FOUND);
        checkedBodyResponse(response, "{\"message\":\"Учетная запись не найдена\"}");
        courierId = courierApi.authorizationCouriers(courier).jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Если нет поля password, запрос возвращает ошибку 400")
    @Description("Отправляем запрос без поля password")
    public void authorizationCourierWithoutPasswordFieldTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        courierApi.createCourier(courier);
        Courier courierWithoutPassword = new Courier(LOGIN_COURIER);
        Response response = courierApi.authorizationCouriers(courierWithoutPassword);
        checkedStatusResponse(response, SC_BAD_REQUEST);
        checkedBodyResponse(response, "{\"message\":\"Недостаточно данных для входа\"}");
        courierId = courierApi.authorizationCouriers(courier).jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Если нет поля login, запрос возвращает ошибку 400")
    @Description("Отправляем запрос без поля login")
    public void authorizationCourierWithoutLoginFieldTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        courierApi.createCourier(courier);
        String courierWithoutLogin = String.format("{\"password\": \"%s\"}", PASSWORD_COURIER);
        Response response = given().header("Content-type", "application/json").and().body(courierWithoutLogin).when().post(AUTHORIZATION_COURIER);
        checkedStatusResponse(response, SC_BAD_REQUEST);
        checkedBodyResponse(response, "{\"message\":\"Недостаточно данных для входа\"}");
        courierId = courierApi.authorizationCouriers(courier).jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку 404")
    public void authorizationUnregisteredUserTest() {
        shouldDeleteCourier = false;
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        Response response = courierApi.authorizationCouriers(courier);
        checkedStatusResponse(response, SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    public void successfulRequestReturnsIdTest() {
        Courier courier = new Courier(LOGIN_COURIER, PASSWORD_COURIER);
        courierApi.createCourier(courier);
        Response response = courierApi.authorizationCouriers(courier);
        checkedIdResponse(response);
        courierId = response.jsonPath().getInt("id");
    }

    @Step("Проверка статуса ответа")
    public void checkedStatusResponse(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка тела ответа")
    public void checkedBodyResponse(Response response, String responseBody) {
        response.then().body(equalTo(responseBody));
    }

    @Step("Проверка что id присутствует и не пустой")
    public void checkedIdResponse (Response response) {
        response.then().assertThat().body("id", notNullValue());
    }

    @AfterEach
    public void deleteCourier() {
        if (shouldDeleteCourier) {
            courierApi.deleteCourierRequest(courierId);
        }
    }

}
