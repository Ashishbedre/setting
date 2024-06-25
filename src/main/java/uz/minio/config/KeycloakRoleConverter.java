package uz.minio.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        final Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get("resource_access");

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            authorities.addAll(((List<String>) realmAccess.get("roles")).stream()
                    .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                    .collect(Collectors.toList()));
        }

        if (resourceAccess != null) {
            for (String resource : resourceAccess.keySet()) {
                Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get(resource);
                if (clientResource.containsKey("roles")) {
                    authorities.addAll(((List<String>) clientResource.get("roles")).stream()
                            .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                            .collect(Collectors.toList()));
                }
            }
        }

        return authorities;
    }
}
