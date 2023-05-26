package com.example.heart_field.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parseObj(String jsonString, Class<T> valueType) throws IOException, JsonProcessingException {
        return objectMapper.readValue(jsonString, valueType);
    }
}
