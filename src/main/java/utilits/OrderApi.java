package utilits;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    public static final String CREATE_ORDER = "/api/v1/orders";
    public static final String CANCEL_ORDER = "/api/v1/orders/cancel";
    public static final String GET_ORDER_LIST = "/api/v1/orders";

    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return given().header("Content-type", "application/json").and().body(order).when().post(CREATE_ORDER);
    }

    @Step("Отмена заказа")
    public void cancelOrderRequest(int trackOrder) {
        String json = String.format("\"track\": %s", trackOrder);
        given().header("Content-type", "application/json").and().body(json).when().put(CANCEL_ORDER);
    }

    @Step("Отправляем Get запрос на получение списка заказов без передачи параметров")
    public Response orderListRequest() {
        return given().get(GET_ORDER_LIST);
    }
}
