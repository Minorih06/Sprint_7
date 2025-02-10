import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import utilits.Order;
import utilits.OrderApi;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;
import static utilits.constants.Constants.*;
import static org.apache.http.HttpStatus.*;

public class OrderCreateParametrizedTest {

    private static final String FIRST_NAME = "Mefodiy";
    private static final String LAST_NAME = "Petrovich";
    private static final String ADDRESS = "Konoha, 142 apt.";
    private static final int METRO_STATION = 4;
    private static final String PHONE = "+7 800 355 35 35";
    private static final int RENT_TIME = 5;
    private static final String DELIVERY_DATE = "2020-06-06";
    private static final String COMMENT =  "Mefodiy, come back to Konoha";
    private static final String[] COLOR_BLACK = {"BLACK"};
    private static final String[] COLOR_GREY = {"GREY"};
    private static final String[] COLOR_BLACK_AND_GREY = {"BLACK", "GREY"};

    private int trackOrder;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    public static Stream<Order> orderProvider() {
        return Stream.of(
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT, COLOR_BLACK), //чёрный цвет
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT, COLOR_GREY), //серый цвет
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT, COLOR_BLACK_AND_GREY), //два цвета
                new Order(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE, RENT_TIME, DELIVERY_DATE, COMMENT)//без указания цвета
        );
    }

    @ParameterizedTest
    @MethodSource("orderProvider")
    @DisplayName("Создание заказа")
    public void createOrderTest(Order order) {
        OrderApi orderApi = new OrderApi();
        Response response = orderApi.createOrder(order);
        checkedStatusResponse(response, SC_CREATED);
        checkedBodyResponse(response);
        trackOrder = response.jsonPath().getInt("track");
    }

    @Step("Проверка статуса ответа")
    public void checkedStatusResponse(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка что track присутствует в ответе и не пустой")
    public void checkedBodyResponse (Response response) {
        response.then().assertThat().body("track", notNullValue());
    }

    @AfterEach
    public void cancelOrder() {
        OrderApi orderApi = new OrderApi();
        orderApi.cancelOrderRequest(trackOrder);
    }
}
