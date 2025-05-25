package com.connectly_cm.Connectly_CM.security;

import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.utils.jwtUtils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        /* Fetching Authorization header (Tokens are passed here) */
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer")) {
            /* If Authorization Header is not valid, pass to next filter */
            filterChain.doFilter(request, response);
            return;
        }
        /* The user won't be authenticated if there is no token
         * But that's okay. Sometimes we may access public urls (unauthorized uses can access)
         * Only authenticated users will be able to access authorized URLS
         * (Exception will be thrown, if unauthorized users access protected URls)*/

        /*Removing Bearer*/
        String token = header.substring(7);

        /*If token expires, exceptions can be thrown while creating claims*/
        try {
            if (!jwtUtils.isTokenExpired(token)) {
                User user = jwtUtils.decodeJwt(token);
                if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken
                            (user.getEmail(), null,
                                    user.getRoles().stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList()));
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    /*Set authentication in SecurityContext*/
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException | SignatureException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        filterChain.doFilter(request, response);
    }
}
