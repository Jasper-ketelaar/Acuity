package com.acuitybotting.data.flow.messaging.services.client.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/20/2018.
 */
public class AWSSigning {

    private static String cr = "GET\n" +
            "/604080725100/test.fifo\n" +
            "{QSTRING}\n" +
            "host:sqs.us-east-1.amazonaws.com\n" +
            "x-amz-date:{DATE}\n" +
            "\n" +
            "host;x-amz-date\n" +
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    private static String sts = "AWS4-HMAC-SHA256\n" +
             "{DATE}" + "\n" +
            "{DATE_S}/us-east-1/sqs/aws4_request\n" +
            "{HASH}";

    private static String header = "AWS4-HMAC-SHA256 Credential=AKIAIHYKVNPDUZPLRDNQ/20180620/us-east-1/sqs/aws4_request,SignedHeaders=host;x-amz-date,Signature={SIG}";

    private static final SimpleDateFormat awsFlavouredISO8601DateParser = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static final SimpleDateFormat awsFlavouredISO8601DateParserSmaller = new SimpleDateFormat("yyyyMMdd");

    static {
        awsFlavouredISO8601DateParser.setTimeZone(new SimpleTimeZone(0, "GMT"));
        awsFlavouredISO8601DateParserSmaller.setTimeZone(new SimpleTimeZone(0, "GMT"));
    }

    public static String awsV4EncodeURI(CharSequence input, boolean encodeSlash) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z')
                    || (ch >= 'a' && ch <= 'z')
                    || (ch >= '0' && ch <= '9')
                    || ch == '_'
                    || ch == '-'
                    || ch == '~'
                    || ch == '.')
            {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                String hex = encodeUrlString(String.valueOf(ch));
                result.append(hex);
            }
        }
        return result.toString();
    }

    public static String encodeUrlString(String path) {
        String encodedPath = null;
        try {
            encodedPath = URLEncoder.encode(path, "UTF-8");
        }
        catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        // Web browsers do not always handle '+' characters well, use the well-supported '%20' instead.
        encodedPath = encodedPath.replaceAll("\\+", "%20");
        // '@' character need not be URL encoded and Google Chrome balks on signed URLs if it is.
        encodedPath = encodedPath.replaceAll("%40", "@");
        return encodedPath;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] hash(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildQString(Map<String, String> params){
        return params.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> awsV4EncodeURI(entry.getKey(), false) + "=" + awsV4EncodeURI(entry.getValue(), false))
                .collect(Collectors.joining("&amp;"));
    }

    static byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }

    public static void main(String[] args) {
        Date date = new Date();

        String lDate = awsFlavouredISO8601DateParser.format(date);
        String sDate = awsFlavouredISO8601DateParserSmaller.format(date);

        Map<String, String> qParams = new HashMap<>();
        qParams.put("Action", "SendMessage");
        qParams.put("MessageBody", "Hello");
        qParams.put("MessageDeduplicationId", "123");
        qParams.put("MessageGroupId", "channel1");
        qParams.put("Version", "2012-11-05");

        String s = buildQString(qParams);
        cr = cr.replace("{QSTRING}", s).replace("{DATE}", lDate).replace("{DATE_S}", sDate);

        try {
            System.out.println(bytesToHex(hash("GET\n" +
                    "/604080725100/test.fifo\n" +
                    "Action=SendMessage&amp;MessageBody=Hello&amp;MessageDeduplicationId=123&amp;MessageGroupId=channel1&amp;Version=2012-11-05\n" +
                    "host:sqs.us-east-1.amazonaws.com\n" +
                    "x-amz-date:20180620T142912Z\n" +
                    "\n" +
                    "host;x-amz-date\n" +
                    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("Canonical String");
        System.out.println("============================");
        System.out.println(cr);
        System.out.println("============================");

        try {
            sts = sts.replace("{HASH}", bytesToHex(hash(cr))).replace("{DATE}", lDate).replace("{DATE_S}", sDate);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("\nString to sign");
        System.out.println("============================");
        System.out.println(sts);
        System.out.println("============================");


        try {
            byte[] signatureKey = getSignatureKey("", "20180620", "us-east-1", "sqs");

            String s1 = bytesToHex(HmacSHA256(sts, signatureKey));

            System.out.println("\nAuthorization");
            System.out.println("============================");
            System.out.println(s1);
            System.out.println("============================");

            System.out.println("\nHeaders");
            System.out.println("============================");
            System.out.println("Authorization=" + header.replace("{SIG}", s1));
            System.out.println("x-amz-date=" + lDate);
            System.out.println("============================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
