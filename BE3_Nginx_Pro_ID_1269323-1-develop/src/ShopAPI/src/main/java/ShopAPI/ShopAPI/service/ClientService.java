package ShopAPI.ShopAPI.service;

import ShopAPI.ShopAPI.dto.request.clientReauest.CreateClientRequest;
import ShopAPI.ShopAPI.dto.response.clientResponse.ClientResponse;
import ShopAPI.ShopAPI.entity.Address;
import ShopAPI.ShopAPI.entity.Client;
import ShopAPI.ShopAPI.exception.BadRequestException;
import ShopAPI.ShopAPI.exception.OperationNotExcepted;
import ShopAPI.ShopAPI.exception.ResourceNotFoundException;
import ShopAPI.ShopAPI.mapper.AddressMapper;
import ShopAPI.ShopAPI.mapper.ClientMapper;
import ShopAPI.ShopAPI.repository.AddressRepository;
import ShopAPI.ShopAPI.repository.ClientRepository;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AddressRepository addressRepository;

    private final ClientMapper mapper = new ClientMapper();

    public List<ClientResponse> getAllClients(Integer limit, Integer offset) {
        if (limit == null && offset == null) {
            return this.clientRepository.findAll()
                    .stream()
                    .map(mapper::convertForResponse)
                    .collect(Collectors.toList());
        }

        if (limit != null && limit <= 0) {
            throw new BadRequestException("limit must be greater than 0");
        }

        if (offset != null && offset < 0) {
            throw new BadRequestException("offset cannot be negative");
        }

        int pageSize = (limit != null) ? limit : Integer.MAX_VALUE;
        int pageNumber = (offset != null && limit != null) ? offset / limit : 0;

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Client> page = clientRepository.findAll(pageable);
        return page.getContent().stream().map(mapper::convertForResponse).collect(Collectors.toList());
    }

    public Client saveClient(CreateClientRequest request) {
        Client client = new ClientMapper().convertClientForService(request);
        Address address = new AddressMapper().convertAddressForService(request.getAddress());
        Optional<Address> optionalAddress = this.addressRepository.findByCountryIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(client.getAddress().getCountry(),
                client.getAddress().getCity(),
                client.getAddress().getStreet());
        if (optionalAddress.isEmpty()) {
            address = this.addressRepository.save(address);
        } else {
            address = optionalAddress.get();
        }
        client.setAddress(address);
        client = this.clientRepository.save(client);
        return client;

    }

    public Client deleteClientByUUID(UUID id) {
        Optional<Client> optionalClient = this.clientRepository.findById(id);
        Client client = optionalClient.orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        this.clientRepository.deleteById(id);
        return client;
    }

    public List<ClientResponse> searchByNameAndSurname(String name, String surname) {
        List<ClientResponse> clientResponses = new ArrayList<>();
        this.clientRepository.findByClientNameIgnoreCaseAndClientSurnameIgnoreCase(name, surname).forEach(e->clientResponses.add(this.mapper.convertForResponse(e)));
        return clientResponses;
    }

    public Client changeAddress(UUID id, Address address) {
        Optional<Client> clientOptional = this.clientRepository.findById(id);
        Address adr = new Address();
        Client client = clientOptional.orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Optional<Address> optionalAddress = this.addressRepository.findByCountryIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(
                address.getCountry(),
                address.getCity(),
                address.getStreet());
        if (optionalAddress.isEmpty()) {
            adr.setCountry(address.getCountry());
            adr.setCity(address.getCity());
            adr.setStreet(address.getStreet());
            adr = this.addressRepository.save(adr);
            client.setAddress(adr);
        } else {
            client.setAddress(optionalAddress.get());
        }

        this.clientRepository.saveAndFlush(client);
        return client;

    }


    public Client getClientById(UUID id) {
        return this.clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }
}
