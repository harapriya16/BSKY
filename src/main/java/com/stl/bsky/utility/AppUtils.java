package com.stl.bsky.utility;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stl.bsky.common.Sha512;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    @Autowired
    private Sha512 sha512;
    private final String NUMBERS = "0123456789";


    @Value("${captcha.secret.salt}")
    private String captchaSalt;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    public static Map<String, Object> generateOtp() {
        Map<String, Object> map = new HashMap<>();
        map.put("mobileOtp", getRandomNumberString());
        map.put("emailOtp", getRandomNumberString());
        map.put("registrationNo", getRegistrationNo());
        map.put("createdTime", new Date());
        return map;
    }

    public static String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public static long checkOtpTime(String req) {
        long difference = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date date1 = format.parse(getCurrentLocalDateTimeStamp());
            Date date2 = format.parse(req);
            difference = date1.getTime() - date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return difference / (60 * 1000) % 60;
    }


    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String getRegistrationNo() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999999);

        // this will convert any number sequence into 9 character.
        return String.format("%09d", number);
    }

    public static String generateVerificationCode(String mobileNo) {
        String verificationCode = null;
        final Random r = new SecureRandom();

        byte[] salt = new byte[32];
        String enc=Sha512.encrypt(mobileNo+salt);
        System.out.println();
        r.nextBytes(salt);
        String encodedSalt = Base64.encodeBase64String(salt);
        // String encodedMobileAndTime = Base64.encodeBase64String(mobileNo + new Date());

        Random rnd = new Random();
        int number = rnd.nextInt(99999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);

    }

//    public static String captchaDecrypting(String captchaSalt){
//        Sha512.decrypt(captchaSalt)
//
//    }
}
