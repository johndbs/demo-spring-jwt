package com.thinkitdevit.demospringjwt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum ERole {

    ROLE_USER("user"),
    ROLE_MODERATOR("mod"),
    ROLE_ADMIN("admin");

    @Getter
    private final String label;

    private static final Map<String, ERole> roleByLabelMap = Arrays.stream(ERole.values()).collect(Collectors.toMap(ERole::getLabel, Function.identity()));

    public static ERole retrieveByLabel(String label){
        return roleByLabelMap.get(label);
    }

}
