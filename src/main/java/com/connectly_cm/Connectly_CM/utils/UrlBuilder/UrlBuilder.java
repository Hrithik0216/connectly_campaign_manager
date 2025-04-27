package com.connectly_cm.Connectly_CM.utils.UrlBuilder;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class UrlBuilder {
    public static String urlBuilderWithParam(String url, Map<String, Object> map) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        map.forEach((k, v) -> {
            if (v != null) {
                builder.queryParam(k, v);
            }
        });
        return builder.build().encode().toUriString();
    }
}
