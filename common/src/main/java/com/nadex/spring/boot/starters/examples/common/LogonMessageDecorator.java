package com.nadex.spring.boot.starters.examples.common;

import quickfix.Message;
import quickfix.field.*;

public class LogonMessageDecorator {
    public static void decorate(Message message, String apikey, String secret) throws Exception {
        MsgType msgType = new MsgType();
        if (message.getHeader().getField(msgType).getValue().equals("A")) {
            message.setField(new EncryptMethod(EncryptMethod.NONE_OTHER));
            long ts = System.currentTimeMillis();
            String tsString = String.valueOf(ts);
            String generated = SignatureGenerator.generate("public/auth",
                    1L,
                    apikey,
                    secret,
                    ts);
            message.setField(new Username(apikey));
            message.setField(new Password(generated));
            message.setField(new RawData(tsString));
            message.setField(new RawDataLength(tsString.length()));
        }
    }
}
