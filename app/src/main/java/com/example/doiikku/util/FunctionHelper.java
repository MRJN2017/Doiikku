package com.example.doiikku.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FunctionHelper {
    public static String rupiahFormat(int price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return "Rp " + formatter.format(price).replaceAll(",", ".");
    }

    public static String getCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        String reportMonth = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")); // Get full month name
        return reportMonth;
    }
}
