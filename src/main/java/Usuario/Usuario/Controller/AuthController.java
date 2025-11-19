package Usuario.Usuario.Controller;

import Usuario.Usuario.dto.LoginRequest;
import Usuario.Usuario.dto.UsuarioRegistroRequest;
import Usuario.Usuario.model.Usuario;
import Usuario.Usuario.security.JpaUserDetailsService;
import Usuario.Usuario.security.JwtUtils;
import Usuario.Usuario.service.UsuarioService;
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
            // Eliminados en el constructor: UsuarioRepository usuarioRepository,
            // PasswordEncoder encoder
            PasswordEncoder encoder) { // Se mantiene encoder solo si se usa en alguna parte
        this.authManager = authManager;
        this.uds = uds;
        this.jwt = jwt;
        this.usuarioService = usuarioService;
        // this.usuarioRepository = usuarioRepository;
        // this.encoder = encoder;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest body) {
        // Autentica credenciales (email/password)
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                body.getEmail(), body.getPassword()));

        UserDetails user = uds.loadUserByUsername(body.getEmail());
        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user.getUsername());
        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return Map.of("token", access, "refreshToken", refresh, "roles", roles);
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(
            @RequestBody Usuario usuario, // ⬅️ Esperar solo la entidad Usuario en el BODY
            @RequestParam(defaultValue = "CLIENTE") String rol // ⬅️ Recibir el rol por QUERY PARAM
    ) {
        // 1. Llamar al servicio con el objeto Usuario y el rol del RequestParam
        Usuario creado = usuarioService.registrarUsuario(usuario, rol);

        // 2. Limpieza y respuesta
        creado.setPasswordHash("**hidden**");
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String username = jwt.getUsername(body.get("refreshToken"));
        UserDetails user = uds.loadUserByUsername(username);
        return Map.of("token", jwt.generateAccessToken(user));
    }
}