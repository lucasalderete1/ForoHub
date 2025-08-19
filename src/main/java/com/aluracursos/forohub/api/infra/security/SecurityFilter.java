package com.aluracursos.forohub.api.infra.security;

import com.aluracursos.forohub.api.domain.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null){
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var path = request.getRequestURI();

        // ⛔ Evita procesar el filtro en /login (y /usuarios si también es público)
        if (path.equals("/login") || path.equals("/usuarios")) {
            filterChain.doFilter(request, response);
            return;
        }
        var tokenJWT = recuperarToken(request);
        if (tokenJWT != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                var usuario = repository.findByNombre(subject)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                // Token inválido o usuario no encontrado, no hacemos nada
                System.out.println("Token inválido o usuario no encontrado: " + ex.getMessage());
                // También podrías registrar este intento en un logger real
            }
        }
        filterChain.doFilter(request, response);

    }

}
