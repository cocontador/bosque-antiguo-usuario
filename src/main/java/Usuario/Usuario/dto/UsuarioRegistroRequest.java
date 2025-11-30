package Usuario.Usuario.dto;

import Usuario.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para registrar un nuevo usuario junto a su rol.
 * Se usa en AuthController.register()
 */
@Data
@Schema(description = "Datos para registrar un nuevo usuario con su rol asociado")
public class UsuarioRegistroRequest {

    @Schema(
            description = "Objeto con los datos del nuevo usuario (nombre, email, contraseña, etc.)",
            implementation = Usuario.class,
            required = true
    )
    private Usuario usuario;

    @Schema(
            description = "Nombre del rol que se asignará al usuario",
            example = "CLIENTE",
            required = true
    )
    private String nombreRol;
}
