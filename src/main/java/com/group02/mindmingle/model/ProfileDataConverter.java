package com.group02.mindmingle.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class ProfileDataConverter implements AttributeConverter<ProfileData, String> {
    private static final Logger logger = LoggerFactory.getLogger(ProfileDataConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ProfileData data) {
        if (data == null) {
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(data);
            logger.debug("Converting ProfileData to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert ProfileData to JSON", e);
            throw new RuntimeException("Failed to convert ProfileData to JSON", e);
        }
    }

    @Override
    public ProfileData convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            ProfileData profileData = objectMapper.readValue(json, ProfileData.class);
            logger.debug("Converting JSON to ProfileData: {}", profileData);
            return profileData;
        } catch (Exception e) {
            logger.error("Failed to convert JSON to ProfileData: {}", json, e);
            throw new RuntimeException("Failed to convert JSON to ProfileData", e);
        }
    }
}