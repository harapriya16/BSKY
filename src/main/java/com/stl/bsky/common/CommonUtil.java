package com.stl.bsky.common;

import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CommonUtil {
    public static boolean hasFieldExist(String fieldName, Class<?> objectClass) {
        try {
            for (Field field : objectClass.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidMagicType(byte[] byteArray) {
        boolean status = true;
        final List<String> allowedMimeTypes = Arrays.asList("image/jpg", "image/jpeg", "image/png", "application/pdf");
        try {
            String mimeType = Magic.getMagicMatch(byteArray, true).getMimeType();
            System.out.println("MimeMagic: " + mimeType);
            if (!allowedMimeTypes.contains(mimeType)) {
                status = false;
            }
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static void saveFileInPhysicalPath(byte[] bytesArray, String directory, String fileName) throws IOException {
        Path path;
        File dir = new File(directory);
        if (!dir.exists())
            dir.mkdirs();
        path = Paths.get(directory + fileName);
        Files.write(path, bytesArray);
        log.info("Saved File Path : " + path);
    }

    public static String maskEmailAddress(String strEmail, char maskChar)
            throws Exception {

        if (strEmail == null || strEmail.equals(""))
            return "";

        String[] email = strEmail.split("@");

        //mask first part
        String strId = "";
        if (email[0].length() < 4)
            strId = maskString(email[0], 0, email[0].length(), '*');
        else
            strId = maskString(email[0], 2, email[0].length() - 2, '*');

        //now append the domain part to the masked id part
        return strId + "@" + email[1];
    }

    public static String maskString(String strText, int start, int end, char maskChar)
            throws Exception {

        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end)
            throw new Exception("End index cannot be greater than start index");

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

}
