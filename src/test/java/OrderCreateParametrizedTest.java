import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static utilits.constants.Constants.*;

public class OrderCreateParametrizedTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    public static Stream<Order> orderProvider() {
        return Stream.of(
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT, COLOR_BLACK), //один цвет
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT, COLOR_BLACK_AND_GREY), //два цвета
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT) //без указания цвета
        );
    }

    @ParameterizedTest
    @MethodSource("orderProvider")
    @DisplayName("Создание заказа")
    public void createOrderTest(Order order) {
        Response response = createOrder(order);
        checkedStatusResponse(response, 201);
        checkedBodyResponse(response);
        int trackOrder = response.jsonPath().getInt("track");
        cancelOrder(trackOrder);
    }


    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return given().header("Content-type", "application/json").and().body(order).when().post(CREATE_ORDER);
    }

    @Step("Проверка статуса ответа")
    public void checkedStatusResponse(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка что track присутствует в ответе и не пустой")
    public void checkedBodyResponse (Response response) {
        response.then().assertThat().body("track", notNullValue());
    }

    @Step("Отмена заказа")
    public void cancelOrder(int trackOrder) {
        String json = String.format("\"track\": %s", trackOrder);
        given().header("Content-type", "application/json").and().body(json).when().put(CANCEL_ORDER);
    }

}
