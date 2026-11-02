package ShopAPI.ShopAPI.service;

import ShopAPI.ShopAPI.entity.Address;
import ShopAPI.ShopAPI.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    public Address saveAddress(Address address) {
        return this.addressRepository.save(address);
    }




}
