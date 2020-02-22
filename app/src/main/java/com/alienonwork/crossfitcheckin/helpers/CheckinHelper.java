package com.alienonwork.crossfitcheckin.helpers;

public class CheckinHelper {
    public static final String ERROR_AUTO_CHECKIN_DISABLED = "error_disabled_auto_checkin";
    public static final String ERROR_INVALID_USER_ID = "error_invalid_user_id";
    public static final String ERROR_INVALID_TOKEN = "error_invalid_token";
    public static final String ERROR_INVALID_URL = "error_invalid_url";

    public static String translateDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "Terça Feira";
            case 2: return "Quarta Feira";
            case 3: return "Quinta Feira";
            case 4: return "Sexta Feira";
            case 5: return "Sábado";
            case 6: return "Domingo";
            case 0:
            default: return "Segunda Feira";
        }
    }
}
