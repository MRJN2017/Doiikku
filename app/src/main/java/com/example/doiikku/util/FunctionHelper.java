package com.example.doiikku.util;

import java.text.DecimalFormat;

public class FunctionHelper {
    public static String rupiahFormat(int price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return "Rp " + formatter.format(price).replaceAll(",", ".");
    }
}
