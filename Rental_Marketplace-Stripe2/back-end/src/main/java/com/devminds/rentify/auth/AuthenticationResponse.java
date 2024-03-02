package com.devminds.rentify.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private String email;
    private String errorMessage;

    public Map<String, Object> toMap() {
        Map<String, Object> resultMap = new HashMap<>();
        if (token != null) {
            resultMap.put("token", token);
        }
        if (email != null) {
            resultMap.put("email", email);
        }
        if (errorMessage != null) {
            resultMap.put("errorMessage", errorMessage);
        }
        return resultMap;
    }
}
