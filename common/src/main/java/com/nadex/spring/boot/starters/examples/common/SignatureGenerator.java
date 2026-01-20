package com.nadex.spring.boot.starters.examples.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

public class SignatureGenerator {
    public static String generate(String method, long msgSeqNum, String apiKey, String secret, long nonce) throws Exception {
        String payload = method + msgSeqNum + apiKey + "system_labelONEEX" + nonce;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        return Hex.encodeHexString(mac.doFinal(payload.getBytes()));
    }
}
