package tasks.api;

import com.google.gson.Gson;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import model.Token;
import model.User;
import model.booking.Booking;
import model.booking.BookingDates;
import model.booking.BookingInfo;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest {
    private static Playwright playwright;
    private static APIRequestContext request;
    private static Map<String, String> headers = new HashMap<>();
    private static int createdBookingId;
    private static String authToken;


    @BeforeAll
    public static void setUp() {
        playwright = Playwright.create();
        headers.put("Content-Type", "application/json");
        setRequestContent();
    }

    private static void setRequestContent() {
        request = playwright.request()
                .newContext(new APIRequest.NewContextOptions()
                        .setBaseURL("https://restful-booker.herokuapp.com")
                        .setExtraHTTPHeaders(headers));
    }

    @Test
    @Order(1)
    @DisplayName("Create Booking")
    public void createBookingTest() {
        BookingDates bookingDates = BookingDates.builder()
                .checkin("2018-01-01")
                .checkout("2019-01-01")
                .build();
        Booking booking = Booking.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingDates(bookingDates)
                .additionalneeds("Breakfast")
                .build();
        APIResponse response = request.post("/booking", RequestOptions.create().setData(booking));
        BookingInfo bookingInfoBody = new Gson().fromJson(response.text(), BookingInfo.class);
        createdBookingId = bookingInfoBody.getBookingid();
        Assertions.assertTrue(response.ok());
    }

    @Test
    @Order(2)
    @DisplayName("Get booking b id")
    public void getBookingByIdTest() {
        APIResponse response = request.get("/booking/" + createdBookingId);
        Assertions.assertTrue(response.ok());
    }

    @Test
    @Order(3)
    @DisplayName("Create token")
    public void createTokenTest() {
        User user = User.builder()
                .username("admin")
                .password("password123")
                .build();
        APIResponse response = request.post("/auth", RequestOptions.create().setData(user));
        authToken = new Gson().fromJson(response.text(), Token.class).getToken();
        Assertions.assertTrue(response.ok());
    }

    @Test
    @Order(4)
    @DisplayName("Update booking using PATCH")
    public void updateBookingUsingPatchTest() {
        String newName = "Tom";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("firstname", newName);
        headers.put("Cookie", "token=" + authToken);
        setRequestContent();
        APIResponse response = request.patch("/booking/" + createdBookingId, RequestOptions.create().setData(requestBody));
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.ok()),
                () -> Assertions.assertTrue(new Gson()
                        .fromJson(response.text(), Booking.class)
                        .getFirstname().equalsIgnoreCase(newName))
        );
    }

    @Test
    @Order(5)
    @DisplayName("Delete booking by id")
    public void deleteBookingByIdTest() {
        APIResponse response = request.delete("/booking/" + createdBookingId);
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.status() == 201),
                () -> Assertions.assertTrue(response.statusText().equals("Created")),
                () -> Assertions.assertTrue(response.text().equals("Created"))
        );
    }
}
