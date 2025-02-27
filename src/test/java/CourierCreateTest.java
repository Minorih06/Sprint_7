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


import static org.hamcrest.Matchers.equalTo;
import static utilits.constants.Constants.*;
import static org.apache.http.HttpStatus.*;

public class CourierCreateTest {
    private boolean shouldDeleteCourier = true;

    Faker faker = new Faker();
    private final String loginCourier = faker.name().username();
    private final String passwordCourier = faker.internet().password();
    private final String firstNameCourier = faker.name().firstName();

    CourierApi courierApi = new CourierApi();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Курьера можно создать / Успешный запрос возвращает ok: true")
    @Description("При заполнении всех полей валидными значениями получаем 201 и в теле ok: true")
    public void courierCanBeCreatedTest() {
        Courier courier = new Courier(loginCourier, passwordCourier, firstNameCourier);
        Response response = courierApi.createCourier(courier);
        checkedStatusResponse(response, SC_CREATED);
        checkedBodyResponse(response, "{\"ok\":true}");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров / Если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void impossibleToCreateTwoIdenticalCouriersTest() {
        Courier courier = new Courier(loginCourier, passwordCourier, firstNameCourier);
        courierApi.createCourier(courier);
        Response response = courierApi.createCourier(courier);
        checkedStatusResponse(response, SC_CONFLICT);
        checkedBodyResponse(response, "{\"message\":\"Этот логин уже используется\"}");
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля;")
    @Description("Проверяем, что без поля firstName в код ответа будет 201")
    public void createCourierWithoutFirstNameFieldTest() {
        Courier courier = new Courier(loginCourier, passwordCourier, false);
        Response response = courierApi.createCourier(courier);
        checkedStatusResponse(response, SC_CREATED);
    }

    @Test
    @DisplayName("Если одного из обязательных полей нет, запрос возвращает ошибку 400")
    @Description("Проверяем, что при отправке запроса без поля password получаем 400 Bad Request")
    public void requestWithoutPasswordFieldTest() {
        shouldDeleteCourier = false;
        Courier courier = new Courier(loginCourier, false, firstNameCourier);
        Response response = courierApi.createCourier(courier);
        checkedStatusResponse(response, SC_BAD_REQUEST);
        checkedBodyResponse(response, "{\"message\":\"Недостаточно данных для создания учетной записи\"}");
    }

    @Step("Проверка статуса ответа")
    public void checkedStatusResponse(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка тела ответа")
    public void checkedBodyResponse(Response response, String responseBody) {
        response.then().body(equalTo(responseBody));
    }

    @AfterEach
    public void deleteCourier() {
        if (shouldDeleteCourier) {
            Courier courier = new Courier(loginCourier, passwordCourier, false);
            Response response = courierApi.authorizationCouriers(courier);
            int courierId = response.jsonPath().getInt("id");
            courierApi.deleteCourierRequest(courierId);
        }
    }
}
