package Usuario.Usuario.service;

import Usuario.Usuario.model.Rol;
import Usuario.Usuario.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException; // ✅ CORRECCIÓN: Usar esta para Not Found

@Service
@Transactional
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    // ---------- Helpers ----------
    private static String sanitizeNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de rol no puede ser nulo ni vacío");
        }
        return nombre.trim().toUpperCase();
    }

    // ---------- Lecturas ----------
    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Rol obtenerPorNombre(String nombre) {
        String key = sanitizeNombre(nombre);
        // ✅ CORRECCIÓN: Usar NoSuchElementException
        return rolRepository.findByNombre(key)
                .orElseThrow(() -> new NoSuchElementException("Rol con nombre " + key + " no encontrado."));
    }

    @Transactional(readOnly = true)
    public Rol obtenerPorId(Integer id) {
        // ✅ CORRECCIÓN: Usar NoSuchElementException
        return rolRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rol con ID " + id + " no encontrado."));
    }

    // ---------- Escrituras ----------
    /** Crea el rol si no existe y lo retorna (idempotente). */
    public Rol asegurarRol(String nombre) {
        String key = sanitizeNombre(nombre);
        return rolRepository.findByNombre(key)
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setNombre(key);
                    return rolRepository.save(r);
                });
    }

    /** Crea un rol nuevo; lanza error si ya existe. */
    public Rol crear(String nombre) {
        String key = sanitizeNombre(nombre);
        rolRepository.findByNombre(key).ifPresent(r -> {
            throw new IllegalArgumentException("El rol ya existe: " + key);
        });
        Rol r = new Rol();
        r.setNombre(key);
        return rolRepository.save(r);
    }

    public void eliminar(Integer id) {
        if (!rolRepository.existsById(id)) {
            // ✅ CORRECCIÓN: Usar NoSuchElementException
            throw new NoSuchElementException("Rol con ID " + id + " no encontrado para eliminar.");
        }
        rolRepository.deleteById(id);
    }
}
