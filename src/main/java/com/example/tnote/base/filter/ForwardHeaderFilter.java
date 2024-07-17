package com.example.tnote.base.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;


public class ForwardHeaderFilter {

    // swagger -> Forward-proxy
    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
