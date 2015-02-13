package com.zcloud.monitor.alarm.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Jsons {

    private static Logger logger = LoggerFactory.getLogger(Jsons.class);

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }


    public static class UUIDJSONSerializer extends
            JsonSerializer<UUID> {

        @Override
        public void serialize(UUID value, JsonGenerator jgen,
                              SerializerProvider provider) throws IOException,
                JsonProcessingException {
            if (value == null)
                jgen.writeNull();
            else
                jgen.writeString(value.toString().replace("-", ""));
        }

    }

    public static class UUIDJSONDeserializer extends
            JsonDeserializer<UUID> {

        @Override
        public UUID deserialize(JsonParser jp,
                                DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            String str = jp.getText();
            if (str != null) {
                return UUIDFromString(str);
            } else
                return null;
        }

    }

    public static UUID UUIDFromString(String str) {
        if (str == null || str.length() == 0)
            return null;
        if (str.length() == 36)
            return UUID.fromString(str);
        else if (str.length() == 32) {
            // c03148f0-6a8b-11e2-baf9-209add412523
            StringBuilder sb = new StringBuilder(36);
            sb.append(str.substring(0, 8));
            sb.append("-");
            sb.append(str.substring(8, 12));
            sb.append("-");
            sb.append(str.substring(12, 16));
            sb.append("-");
            sb.append(str.substring(16, 20));
            sb.append("-");
            sb.append(str.substring(20));
            return UUID.fromString(sb.toString());
        } else
            throw new IllegalArgumentException(str + " is illegal uuid. ");
    }


    public static String objectToJSONStr(Object obj) {
        if (obj == null)
            return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Failed objectToJSONStr object is : " + obj, e);
            return null;
        }
    }

    public static String objectToPrettyJSONStr(Object obj) {
        if (obj == null)
            return null;
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Failed objectToPrettyJSONStr object is : " + obj, e);
            return null;
        }
    }

    public static <T> T objectFromJSONStr(String str, JavaType javaType) {
        if (str == null || str.length() == 0)
            return null;
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            //logger.error("Failed objectFromJSONStr jsonStr is : " + str + ", javaType is : " + javaType.getRawClass().getCanonicalName(), e);
            return null;
        }
    }

    public static <T> T objectFromJSONStr(String str, Class<T> type) {
        if (str == null || str.length() == 0)
            return null;
        try {
            return objectMapper.readValue(str, type);
        } catch (Exception e) {
            //logger.error("Failed objectFromJSONStr jsonStr is : " + str + ", type is : " + type.getCanonicalName(), e);
            return null;
        }
    }

    public static <T> List<T> listFromJSONStr(String str, Class<T> type) {
        List<T> list = new ArrayList<T>();
        @SuppressWarnings("unchecked")
        List<Object> ncs = Jsons.objectFromJSONStr(str, List.class);
        if (ncs != null && ncs.size() > 0) {
            for (Object nc : ncs) {
                T item = Jsons.objectFromJSONStr(
                        Jsons.objectToJSONStr(nc), type);
                if (item != null)
                    list.add(item);
            }
        }
        return list;
    }

}
