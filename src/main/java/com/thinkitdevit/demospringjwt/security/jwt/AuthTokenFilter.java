package com.thinkitdevit.demospringjwt.security.jwt;

import com.thinkitdevit.demospringjwt.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        tracePathAndParams(request);

        String jwtToken = parseJwt(request);

        if(jwtToken != null && jwtUtils.validateToken(jwtToken)){
            String username=jwtUtils.getUsernameFromToken(jwtToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }

    private static void tracePathAndParams(HttpServletRequest request) {
        String path = request.getRequestURI();
        StringBuilder params = new StringBuilder();
        request.getParameterMap().entrySet().forEach((entry) -> {
            params.append("'"+entry.getKey()+"' : [");
            Arrays.stream(entry.getValue()).forEach(value -> {
                params.append(" '"+value+"' ,");
            });
        });

        log.info("Call url : {} with params {}", path, params);
    }

    private String parseJwt(HttpServletRequest request) {
        String jwtToken=null;

        String requestTokenHeader = request.getHeader("Authorization");
        if(StringUtils.startsWith(requestTokenHeader,"Bearer ")){
            jwtToken = requestTokenHeader.substring(7);
        }
        return jwtToken;
    }
}
