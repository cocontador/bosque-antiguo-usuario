package Usuario.Usuario.Controller;

import Usuario.Usuario.dto.LoginRequest;
import Usuario.Usuario.dto.UsuarioRegistroRequest;
import Usuario.Usuario.model.Usuario;
import Usuario.Usuario.security.JpaUserDetailsService;
import Usuario.Usuario.security.JwtUtils;
import Usuario.Usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JpaUserDetailsService uds;
    private final JwtUtils jwt;
    private final UsuarioService usuarioService;
    // Eliminadas inyecciones no usadas (UsuarioRepository y PasswordEncoder)
    // private final UsuarioRepository usuarioRepository;
    // private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager,
                          JpaUserDetailsService uds,
                          JwtUtils jwt,
                          UsuarioService usuarioService,
                          // Eliminados en el constructor: UsuarioRepository usuarioRepository, PasswordEncoder encoder
                          PasswordEncoder encoder) { // Se mantiene encoder solo si se usa en alguna parte
        this.authManager = authManager;
        this.uds = uds;
        this.jwt = jwt;
        this.usuarioService = usuarioService;
        // this.usuarioRepository = usuarioRepository;
        // this.encoder = encoder;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest body) {
        // Autentica credenciales (email/password)
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                body.getEmail(), body.getPassword()
        ));

        UserDetails user = uds.loadUserByUsername(body.getEmail());
        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user.getUsername());
        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return Map.of("token", access, "refreshToken", refresh, "roles", roles);
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "409", description = "Email ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioRegistroRequest req) {
        // Usa tu service para validar, crear, cifrar pass y asignar rol
        Usuario creado = usuarioService.registrarUsuario(req.getUsuario(), req.getNombreRol());

        // Se recomienda devolver el token, pero se mantiene tu respuesta original:
        // No devolvemos passwordHash al front: limpiamos (Esto debe ir en el Assembler, pero aquí es la única salida JSON)
        creado.setPasswordHash("**hidden**");
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Renovar token", description = "Renueva el token de acceso usando el refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String username = jwt.getUsername(body.get("refreshToken"));
        UserDetails user = uds.loadUserByUsername(username);
        return Map.of("token", jwt.generateAccessToken(user));
    }
}
