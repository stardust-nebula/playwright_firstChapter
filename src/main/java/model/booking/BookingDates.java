package model.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class BookingDates {

    private String checkin;
    private String checkout;

}
