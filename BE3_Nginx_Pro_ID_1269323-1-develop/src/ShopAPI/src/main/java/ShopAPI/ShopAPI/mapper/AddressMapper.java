package ShopAPI.ShopAPI.mapper;

import ShopAPI.ShopAPI.dto.request.addressRequest.AddressRequest;
import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import ShopAPI.ShopAPI.entity.Address;

public class AddressMapper {
    public Address convertAddressForService(AddressRequest request) {
        Address address = new Address();
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setStreet(request.getStreet());
        return address;
    }

    public AddressResponse convertToResponse(Address address) {
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setCountry(address.getCountry());
        addressResponse.setCity(address.getCity());
        addressResponse.setStreet(address.getStreet());
        return addressResponse;
    }

    public Address convertAddressResponseToAddress(AddressResponse address) {
        Address add = new Address();
        add.setCountry(address.getCountry());
        add.setCity(address.getCity());
        add.setStreet(address.getStreet());
        return add;
    }
}
