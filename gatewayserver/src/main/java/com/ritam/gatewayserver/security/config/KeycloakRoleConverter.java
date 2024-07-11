package com.ritam.gatewayserver.security.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String rolePrefix = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {

        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");

        return Optional.ofNullable(realmAccess)
                .map(nonNullRealmAccess -> (List<String>) nonNullRealmAccess.get("roles"))
                .stream()
                .flatMap(listStr -> listStr.stream())
                .map(rolePrefix::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
