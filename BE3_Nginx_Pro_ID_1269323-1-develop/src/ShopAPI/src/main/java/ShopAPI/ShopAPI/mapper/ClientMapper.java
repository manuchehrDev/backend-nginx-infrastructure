package ShopAPI.ShopAPI.mapper;

import ShopAPI.ShopAPI.dto.request.clientReauest.CreateClientRequest;
import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import ShopAPI.ShopAPI.dto.response.clientResponse.ClientDeleteResponse;
import ShopAPI.ShopAPI.dto.response.clientResponse.ClientResponse;
import ShopAPI.ShopAPI.entity.Client;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClientMapper {
    public Client convertClientForService(CreateClientRequest request){
        Client client  = new Client();
        client.setClientName(request.getClientName());
        client.setClientSurname(request.getClientSurname());
        client.setGender(request.getGender().charAt(0));
        client.setBirthday(request.getBirthday());
        client.setRegistrationDate(LocalDateTime.now());
        client.setAddress(new AddressMapper().convertAddressForService(request.getAddress()));
        return client;
    }

    public ClientResponse convertForResponse(Client client){
        ClientResponse response = new ClientResponse();
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setCity(client.getAddress().getCity());
        addressResponse.setCountry(client.getAddress().getCountry());
        addressResponse.setStreet(client.getAddress().getStreet());
        response.setId(client.getId());
        response.setAddress(addressResponse);
        response.setClientName(client.getClientName());
        response.setClientSurname(client.getClientSurname());
        response.setGender(client.getGender());
        response.setBirthday(client.getBirthday());
        response.setRegistrationDate(client.getRegistrationDate());
        return response;
    }
}
