package utilits.constants;

public class Constants {
    //URL стенда
    public static final String URL = "https://qa-scooter.praktikum-services.ru";

    //Ручки
    public static final String CREATE_COURIER = "/api/v1/courier";
    public static final String AUTHORIZATION_COURIER = "/api/v1/courier/login";
    public static final String DELETE_COURIER = "/api/v1/courier/{id}";
    public static final String CREATE_ORDER = "/api/v1/orders";
    public static final String CANCEL_ORDER = "/api/v1/orders/cancel";
    public static final String GET_ORDER_LIST = "/api/v1/orders";

    //Данные курьера
    public static final String LOGIN_COURIER = "killagram";
    public static final String PASSWORD_COURIER = "1234";
    public static final String FIRST_NAME_COURIER = "Afanasiy";

    //Данные заказа
    public static final String FIRST_NAME = "Mefodiy";
    public static final String LAST_NAME = "Petrovich";
    public static final String ADDRESS = "Konoha, 142 apt.";
    public static final int METRO_STATION = 4;
    public static final String PHONE = "+7 800 355 35 35";
    public static final int RENT_TIME = 5;
    public static final String DELIVERY_DATE = "2020-06-06";
    public static final String COMMENT =  "Mefodiy, come back to Konoha";
    public static final String[] COLOR_BLACK = {"BLACK"};
    public static final String[] COLOR_BLACK_AND_GREY = {"BLACK", "GREY"};

}
