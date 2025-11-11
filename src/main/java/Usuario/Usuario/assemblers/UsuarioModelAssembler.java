package Usuario.Usuario.assemblers;

import Usuario.Usuario.Controller.UsuarioController;
import Usuario.Usuario.model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {

        // ✅ CORRECCIÓN: Ocultar el hash de la contraseña en el modelo de salida
        usuario.setPasswordHash("**hidden**");


        return EntityModel.of(
                usuario,

                // self: GET /usuarios/{id}
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId()))
                        .withSelfRel(),

                // colección: GET /usuarios
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios())
                        .withRel("usuarios"),

                // eliminar: DELETE /usuarios/{id}
                linkTo(methodOn(UsuarioController.class)
                        .eliminarUsuario(usuario.getId()))
                        .withRel("eliminar"),

                // actualizar: PUT /usuarios/{id}
                linkTo(methodOn(UsuarioController.class)
                        .actualizarUsuario(usuario.getId(), null))
                        .withRel("actualizar")
        );
    }
}