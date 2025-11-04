package com.wcs.travel_blog.geocoding;

import java.util.HashMap;
import java.util.Map;

final class ContinentLookup {

    private static final Map<String, String> MAP = new HashMap<>();

    static {
        put(new String[]{
                "DZ", "AO", "BJ", "BW", "BF", "BI", "CM", "CV", "CF", "TD", "KM", "CG", "CD", "CI", "DJ", "EG", "GQ",
                "ER", "SZ", "ET", "GA", "GM", "GH", "GN", "GW", "KE", "LS", "LR", "LY", "MG", "MW", "ML", "MR", "MU",
                "YT", "MA", "MZ", "NA", "NE", "NG", "RE", "RW", "SH", "ST", "SN", "SC", "SL", "SO", "ZA", "SS", "SD",
                "TZ", "TG", "TN", "UG", "EH", "ZM", "ZW"
        }, "Africa");

        put(new String[]{
                "AF", "AM", "AZ", "BH", "BD", "BT", "BN", "KH", "CN", "CX", "CC", "CY", "GE", "HK", "IN", "ID", "IR",
                "IQ", "IL", "JP", "JO", "KZ", "KW", "KG", "LA", "LB", "MO", "MY", "MV", "MN", "MM", "NP", "KP", "OM",
                "PK", "PS", "PH", "QA", "SA", "SG", "KR", "LK", "SY", "TW", "TJ", "TH", "TL", "TR", "TM", "AE", "UZ",
                "VN", "YE"
        }, "Asia");

        put(new String[]{
                "AL", "AD", "AT", "BY", "BE", "BA", "BG", "HR", "CZ", "DK", "EE", "FO", "FI", "FR", "DE", "GI", "GR",
                "GG", "HU", "IS", "IE", "IM", "IT", "JE", "LV", "LI", "LT", "LU", "MT", "MC", "MD", "ME", "NL", "MK",
                "NO", "PL", "PT", "RO", "RU", "SM", "RS", "SK", "SI", "ES", "SJ", "SE", "CH", "UA", "GB", "VA"
        }, "Europe");

        put(new String[]{
                "AI", "AG", "AW", "BS", "BB", "BZ", "BM", "VG", "CA", "KY", "CR", "CU", "CW", "DM", "DO", "SV", "GL",
                "GD", "GP", "GT", "HT", "HN", "JM", "MQ", "MX", "MS", "NI", "PA", "PR", "BL", "KN", "LC", "MF", "PM",
                "VC", "SX", "TT", "TC", "US", "VI", "BQ"
        }, "North America");

        put(new String[]{
                "AR", "BO", "BR", "CL", "CO", "EC", "FK", "GF", "GY", "PY", "PE", "SR", "UY", "VE"
        }, "South America");

        put(new String[]{
                "AS", "AU", "CK", "FJ", "PF", "GU", "KI", "MH", "FM", "NR", "NC", "NZ", "NU", "NF", "MP", "PW", "PG",
                "PN", "WS", "SB", "TK", "TO", "TV", "UM", "VU", "WF"
        }, "Oceania");

        put(new String[]{
                "AQ", "BV", "TF", "HM", "GS"
        }, "Antarctica");
    }

    private ContinentLookup() {
    }

    private static void put(String[] codes, String continent) {
        for (String code : codes) {
            MAP.put(code.toUpperCase(), continent);
        }
    }

    static String lookup(String countryCode) {
        if (countryCode == null) {
            return null;
        }
        return MAP.get(countryCode.toUpperCase());
    }
}
