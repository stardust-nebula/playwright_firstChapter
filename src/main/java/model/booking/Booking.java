package model.booking;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Booking {

    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    @SerializedName("bookingdates")
    private BookingDates bookingDates;
    private String additionalneeds;

}
