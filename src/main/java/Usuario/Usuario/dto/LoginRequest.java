package Usuario.Usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Credenciales de acceso para login")
public class LoginRequest {

    @Schema(description = "Correo electrónico del usuario", example = "usuario@correo.com", required = true)
    private String email;

    @Schema(description = "Contraseña del usuario", example = "1234", required = true)
    private String password;
}