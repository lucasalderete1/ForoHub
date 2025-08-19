package com.aluracursos.forohub.api.domain.usuario;

public record DatosDetalleUsuario(
       Long id,
       String nombre,
       String correoElectronico,
       Perfil perfil
) {
    public DatosDetalleUsuario(Usuario usuario) {
        this(usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreoElectronico(),
                usuario.getPerfil());
    }

}
