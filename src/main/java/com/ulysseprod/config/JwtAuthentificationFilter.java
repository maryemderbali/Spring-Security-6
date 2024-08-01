package com.ulysseprod.config;

import com.ulysseprod.Entities.Token;
import com.ulysseprod.Entities.TokenType;
import com.ulysseprod.Repositories.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.hibernate.grammars.hql.HqlParser.CURRENT_TIMESTAMP;

@Component
@RequiredArgsConstructor

public class JwtAuthentificationFilter extends OncePerRequestFilter {

    private final JwtService JwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

       final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username ;

        if (authHeader== null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request ,response);
            return;
        }

        jwt = authHeader.substring(7);
        username = JwtService.extractusername(jwt) ;
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isRevoked() && t.getTokenType() == TokenType.BEARER)
                    .orElse(false);

            if (JwtService.isTokenValid(jwt,userDetails) && isTokenValid){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken
                                (userDetails,
                                        null,
                                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);

    }
}
