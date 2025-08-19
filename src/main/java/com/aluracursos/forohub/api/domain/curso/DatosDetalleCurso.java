package com.aluracursos.forohub.api.domain.curso;

public record DatosDetalleCurso(
        Long id,
        String nombreCurso,
        CategoriaCurso categoriaCurso
) {
    public DatosDetalleCurso(Curso curso) {
        this(curso.getId(), curso.getNombreCurso(), curso.getCategoriaCurso());
    }
}
