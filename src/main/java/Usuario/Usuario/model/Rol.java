package Usuario.Usuario.model;

import jakarta.persistence.*;

@Entity
public class Rol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre; // ADMIN, VENDEDOR, CLIENTE

    // ✅ RECOMENDACIÓN: Constructor por defecto
    public Rol() {
    }

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    // getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}