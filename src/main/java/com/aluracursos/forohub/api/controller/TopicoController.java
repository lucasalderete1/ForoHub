package com.aluracursos.forohub.api.controller;


import com.aluracursos.forohub.api.domain.curso.Curso;
import com.aluracursos.forohub.api.domain.curso.CursoRepository;
import com.aluracursos.forohub.api.domain.topico.*;
import com.aluracursos.forohub.api.domain.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topicos")

public class TopicoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Transactional
    @PostMapping
    public ResponseEntity registrar(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriBuilder) {
        //control de negocio
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            return ResponseEntity.badRequest().body("Ya existe un tópico con el mismo título y mensaje");
        }

        //obtengo relaciones entre topico usuario y curso
        var autor = usuarioRepository.getReferenceById(datos.idAutor());
        var curso = cursoRepository.getReferenceById(datos.idCurso());
        //creo objeto topico
        var topico = new Topico(datos, autor, curso);
        //guardo el objeto en la base
        topicoRepository.save(topico);

        //devuelvo 201 Created con la URI y JSON en Body
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleTopico(topico));
    }

//Devuelve una lista de todos los topicos sin ordenar
    //@GetMapping
    //public ResponseEntity<List<DatosListadoTopico>> listarTopicos() {
    //var listaTopicos = topicoRepository.findAll()
    // .stream()
    // .map(DatosListadoTopico::new)
    // .toList();
    //return ResponseEntity.ok(listaTopicos);
    //}

    //Devuelve una página con los tópicos ordenados según criterio
    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listarTopicos(@PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion) {
        var page = topicoRepository.findAll(paginacion)
                .map(DatosListadoTopico::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detallarTopico(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("El ID proporcionado no es válido.");
        }
        var topicoBuscado = topicoRepository.findById(id);
        if (topicoBuscado.isEmpty()) {
            Map<String, String> error = Map.of("error", "No existe tópico con este ID.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        }
        var topico = topicoBuscado.get();
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTopico(@PathVariable Long id, @RequestBody @Valid DatosActualizarTopico datos) {
        var topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isPresent()) {
            var topico = topicoOptional.get();
            topico.actualizarDatos(datos);
            return ResponseEntity.ok(new DatosDetalleTopico(topico));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe tópico con este ID."));
        }

    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTopico(@PathVariable Long id) {
        if(topicoRepository.existsById(id)){
            topicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe tópico con este ID."));

        }
    }
}