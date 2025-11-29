package Usuario.Usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_seq")
    @SequenceGenerator(name = "rol_seq", sequenceName = "ROL_SEQ", allocationSize = 1)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre; // ADMIN, VENDEDOR, CLIENTE
}
