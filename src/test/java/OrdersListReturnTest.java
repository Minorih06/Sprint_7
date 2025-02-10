import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilits.OrderApi;

import static org.hamcrest.Matchers.notNullValue;
import static utilits.constants.Constants.*;

public class OrdersListReturnTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Тело ответа возвращается список заказов")
    public void ordersListReturnIsNotNullTest() {
        OrderApi orderApi = new OrderApi();
        Response response = orderApi.orderListRequest();
        checkedOrdersIsNotNull(response);
    }


    @Step("Проверяем получения списка заказов")
    public void checkedOrdersIsNotNull(Response response) {
        response.then().assertThat().body("orders", notNullValue());
    }
}
