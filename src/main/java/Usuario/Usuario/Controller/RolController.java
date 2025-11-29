package Usuario.Usuario.Controller;

import Usuario.Usuario.model.Rol;
import Usuario.Usuario.service.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<Rol> listar() {
        return rolService.listar();
    }

    @GetMapping("/{id}")
    public Rol obtener(@PathVariable Integer id) {
        return rolService.obtenerPorId(id);
    }

    @GetMapping("/nombre/{nombre}")
    public Rol obtenerPorNombre(@PathVariable String nombre) {
        return rolService.obtenerPorNombre(nombre);
    }

    @PostMapping
    public ResponseEntity<Rol> crear(@RequestParam String nombre) {
        Rol creado = rolService.crear(nombre);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
