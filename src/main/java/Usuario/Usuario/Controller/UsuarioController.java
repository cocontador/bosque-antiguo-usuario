package Usuario.Usuario.Controller;

import Usuario.Usuario.assemblers.UsuarioModelAssembler;
import Usuario.Usuario.model.Usuario;
import Usuario.Usuario.service.UsuarioService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    public UsuarioController(UsuarioService usuarioService, UsuarioModelAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<EntityModel<Usuario>> models = usuarioService.listarUsuarios().stream()
                // ✅ CORRECCIÓN: El hash se oculta ahora en el Assembler, no aquí
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(models);
    }

    @GetMapping("/{id}")
    public EntityModel<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario u = usuarioService.obtenerUsuarioPorId(id);
        // ✅ CORRECCIÓN: El hash se oculta ahora en el Assembler, no aquí
        return assembler.toModel(u);
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario,
                                                @RequestParam(defaultValue = "CLIENTE") String rol) {
        Usuario creado = usuarioService.registrarUsuario(usuario, rol);
        // ✅ CORRECCIÓN: El hash se oculta ahora en el Assembler, no aquí
        // Se recomienda usar DTOs y devolver el token aquí, pero mantenemos tu firma.
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario cambios) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, cambios);
        // ✅ CORRECCIÓN: El hash se oculta ahora en el Assembler, no aquí
        return actualizado;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles/{rol}")
    public Usuario asignarRol(@PathVariable Long id, @PathVariable String rol) {
        Usuario u = usuarioService.asignarRol(id, rol);
        // ✅ CORRECCIÓN: El hash se oculta ahora en el Assembler, no aquí
        return u;
    }

    @DeleteMapping("/{id}/roles/{rol}")
    public Usuario quitarRol(@PathVariable Long id, @PathVariable String rol) {
        Usuario u = usuarioService.quitarRol(id, rol);
        // ✅ CORRECCIÓN: El hash se oculta ahora en el Assembler, no aquí
        return u;
    }
}
