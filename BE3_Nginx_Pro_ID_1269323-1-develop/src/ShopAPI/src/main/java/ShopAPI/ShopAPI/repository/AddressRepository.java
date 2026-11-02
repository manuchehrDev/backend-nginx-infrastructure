package ShopAPI.ShopAPI.repository;

import ShopAPI.ShopAPI.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByCountryIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(
            String country, String city, String street);
}
