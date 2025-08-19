package com.aluracursos.forohub.api.infra.exceptions;

import jakarta.persistence.EntityNotFoundException;
import com.aluracursos.forohub.api.domain.ValidacionException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GestorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> gestionarError404() {
        return ResponseEntity.status(404).body(new DatosError("El recurso solicitado no fue encontrado."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> gestionarError400(MethodArgumentNotValidException ex) {
        var errores = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(
                errores.stream().map(DatosErrorValidacion::new).toList()
        );
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<?> gestionarErrorDeValidacion(ValidacionException e) {
        return ResponseEntity.badRequest().body(new DatosError(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> manejarBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(new DatosError("Credenciales inválidas"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> manejarAuthException(AuthenticationException ex) {
        return ResponseEntity.status(401).body(new DatosError("No fue posible autenticar al usuario"));
    }

    // DTO para errores de validación de campos
    public record DatosErrorValidacion(String campo, String mensaje) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    // DTO genérico para errores simples
    public record DatosError(String mensaje) {}
}
