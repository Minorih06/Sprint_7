package utilits;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

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
        Response response = given().header("Content-type", "application/json").and().queryParam("track", trackOrder).when().put(CANCEL_ORDER);
        response.then().statusCode(SC_OK);
    }

    @Step("Отправляем Get запрос на получение списка заказов без передачи параметров")
    public Response orderListRequest() {
        return given().get(GET_ORDER_LIST);
    }
}
