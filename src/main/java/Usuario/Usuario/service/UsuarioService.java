package Usuario.Usuario.service;

import Usuario.Usuario.model.Rol;
import Usuario.Usuario.model.Usuario;
import Usuario.Usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ Añadido para la clase
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional // ✅ Asegurar que la clase sea transaccional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolService rolService; // ✅ CORRECCIÓN: Usar RolService
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolService rolService, // ✅ Inyección RolService
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= LISTAR / OBTENER =================
    @Transactional(readOnly = true) // ✅ Mejor práctica para lectura
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true) // ✅ Mejor práctica para lectura
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
    }

    // ================= CREAR / REGISTRAR =================
    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        // valida email único
        usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        });

        // ✅ CORRECCIÓN: USAR ROL SERVICE PARA CENTRALIZAR LOGICA
        Rol rol = rolService.asegurarRol(nombreRol);

        // encripta contraseña y setea estado
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        usuario.setActivo(true);
        usuario.getRoles().add(rol);

        return usuarioRepository.save(usuario);
    }

    // ================= ACTUALIZAR =================
    public Usuario actualizarUsuario(Long id, Usuario cambios) {
        Usuario u = obtenerUsuarioPorId(id);

        if (cambios.getNombre() != null)
            u.setNombre(cambios.getNombre());
        if (cambios.getEmail() != null && !cambios.getEmail().equalsIgnoreCase(u.getEmail())) {
            usuarioRepository.findByEmail(cambios.getEmail()).ifPresent(x -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
            });
            u.setEmail(cambios.getEmail());
        }
        if (cambios.getPasswordHash() != null && !cambios.getPasswordHash().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(cambios.getPasswordHash()));
        }

        // 🚨 Estos campos ya existen en Usuario.java después de la corrección 1
        if (cambios.getDireccion() != null)
            u.setDireccion(cambios.getDireccion());
        if (cambios.getRut() != null)
            u.setRut(cambios.getRut());
        if (cambios.getApellido() != null)
            u.setApellido(cambios.getApellido());
        // si quieres permitir cambiar activo:
        // u.setActivo(cambios.isActivo());

        return usuarioRepository.save(u);
    }

    // ================= ELIMINAR =================
    public void eliminarUsuario(Long id) {
        Usuario u = obtenerUsuarioPorId(id);
        usuarioRepository.delete(u);
    }

    // ================= ROLES =================
    public Usuario asignarRol(Long idUsuario, String nombreRol) {
        Usuario u = obtenerUsuarioPorId(idUsuario);
        // ✅ CORRECCIÓN: USAR ROL SERVICE PARA CENTRALIZAR LOGICA
        Rol rol = rolService.asegurarRol(nombreRol);

        u.getRoles().add(rol);
        return usuarioRepository.save(u);
    }

    public Usuario quitarRol(Long idUsuario, String nombreRol) {
        Usuario u = obtenerUsuarioPorId(idUsuario);
        u.getRoles().removeIf(r -> r.getNombre().equalsIgnoreCase(nombreRol));
        return usuarioRepository.save(u);
    }
}