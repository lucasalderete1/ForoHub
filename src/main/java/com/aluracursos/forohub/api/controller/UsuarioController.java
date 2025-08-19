package com.aluracursos.forohub.api.controller;


import com.aluracursos.forohub.api.domain.usuario.DatosDetalleUsuario;
import com.aluracursos.forohub.api.domain.usuario.DatosRegistroUsuario;
import com.aluracursos.forohub.api.domain.usuario.Usuario;
import com.aluracursos.forohub.api.domain.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping
    public ResponseEntity registrar(@RequestBody @Valid DatosRegistroUsuario datos, UriComponentsBuilder uriComponentsBuilder) {
        // Validar si el correo ya existe
        if (repository.existsByCorreoElectronico(datos.correoElectronico())) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        // Hashear la contraseña antes de construir el usuario
        String contrasenaHasheada = passwordEncoder.encode(datos.contrasena());
        var usuario = new Usuario(datos.nombre(),
                datos.correoElectronico(),
                contrasenaHasheada,
                datos.perfil());
        repository.save(usuario);

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleUsuario(usuario));
    }
}
