package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState checkState(String stateParam) {
        BookingState[] values = BookingState.values();

        for (BookingState val : values) {
            if (stateParam.toUpperCase().equals(val.name())) {
                return val;
            }
        }

        throw new IllegalStateException("Unknown state: " + stateParam);
    }
}
