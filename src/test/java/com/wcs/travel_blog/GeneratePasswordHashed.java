package com.wcs.travel_blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHashed {
    @Test
    void printHashes() {
        var enc = new BCryptPasswordEncoder();
        System.out.println(enc.encode("password123"));
        System.out.println(enc.encode("password456"));
        System.out.println(enc.encode("password789"));
    }
}
