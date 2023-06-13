package com.example.heart_field.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 11:15 PM
 */
public class TimeUtil {

    /**
     *
     * @return
     */
    public static LocalDateTime getDayStart() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    public static LocalDateTime getDayStart(LocalDateTime time) {
        LocalDate thatDay = time.toLocalDate();
        return LocalDateTime.of(thatDay, LocalTime.MIN);
    }

    /**
     * 使用localdatetime获取当天24点的值
     *
     * @return
     */

    public static LocalDateTime getDayEnd() {

        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

    }

    public static LocalDateTime getDayEnd(LocalDateTime time) {

        LocalDate thatDay = time.toLocalDate();
        return LocalDateTime.of(thatDay, LocalTime.MAX);

    }

    public static LocalDateTime getWeekStart() {
        return LocalDateTime.of(LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1), LocalTime.MIN);
    }

}
