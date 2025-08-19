package com.aluracursos.forohub.api.domain.curso;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "cursos")
@Entity(name = "Curso")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCurso;

    @Enumerated(EnumType.STRING)
    private CategoriaCurso categoriaCurso;

    public Curso(DatosRegistroCurso datos) {
        this.nombreCurso = datos.nombre();
        this.categoriaCurso = datos.categoriaCurso();
    }
}
