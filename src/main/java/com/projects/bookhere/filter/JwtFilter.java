package com.projects.bookhere.filter;

import com.projects.bookhere.model.Authority;
import com.projects.bookhere.repository.AuthorityRepository;
import com.projects.bookhere.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/* Get token information from request header and store information into SecurityContextHolder */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";   //Request header key
    private final String PREFIX = "Bearer ";   //Token type
    private AuthorityRepository authorityRepository;
    private JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        //Get token from header
        final String authorizationHeader = httpServletRequest.getHeader(HEADER);
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            jwt = authorizationHeader.substring(7);
        }

        //Store information in SecurityContextHolder
        // Spring will perform authorization on subsequent requests, and map information tp principal
        if (jwt != null && jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtil.extractUsername(jwt);
            Authority authority = authorityRepository.findById(username).orElse(null);
            if (authority != null) {
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
