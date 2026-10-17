package org.example.tourismchengdu.util;

import java.util.regex.Pattern;

public final class IdCardUtils {

    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$"
    );

    private IdCardUtils() {
    }

    public static boolean isValid(String idCardNo) {
        return idCardNo != null && ID_CARD_PATTERN.matcher(idCardNo).matches();
    }

    public static String mask(String idCardNo) {
        if (idCardNo == null || idCardNo.length() < 8) {
            return idCardNo;
        }
        return idCardNo.substring(0, 6) + "********" + idCardNo.substring(idCardNo.length() - 4);
    }
}
