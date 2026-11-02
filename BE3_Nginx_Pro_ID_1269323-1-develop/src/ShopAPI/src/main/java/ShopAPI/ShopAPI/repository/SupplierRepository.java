package ShopAPI.ShopAPI.repository;

import ShopAPI.ShopAPI.entity.Supplier;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "Название поставщика обязательно") String name);
}
