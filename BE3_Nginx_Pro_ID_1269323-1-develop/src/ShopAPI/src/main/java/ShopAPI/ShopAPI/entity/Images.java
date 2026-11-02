package ShopAPI.ShopAPI.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //    @Lob
//    @Type(byte[].class)
    @Column(name = "image", columnDefinition = "BYTEA", nullable = false)
    private byte[] image;
}

