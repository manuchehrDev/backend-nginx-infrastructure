package ShopAPI.ShopAPI.service;


import ShopAPI.ShopAPI.dto.request.supplierRequest.SupplierRequest;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import ShopAPI.ShopAPI.entity.Address;
import ShopAPI.ShopAPI.entity.Supplier;
import ShopAPI.ShopAPI.exception.BadRequestException;
import ShopAPI.ShopAPI.exception.DuplicateResourceException;
import ShopAPI.ShopAPI.exception.OperationNotExcepted;
import ShopAPI.ShopAPI.exception.ResourceNotFoundException;
import ShopAPI.ShopAPI.mapper.AddressMapper;
import ShopAPI.ShopAPI.mapper.SupplierMapper;
import ShopAPI.ShopAPI.repository.AddressRepository;
import ShopAPI.ShopAPI.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private AddressRepository addressRepository;

    private final SupplierMapper supplierMapper = new SupplierMapper();
    private final AddressMapper addressMapper = new AddressMapper();


    public Supplier saveSupplier(SupplierRequest request) {
        Supplier supplier = this.supplierMapper.convertToService(request);
        if (supplierRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Supplier already exists");
        }
        Optional<Address> address = this.addressRepository.
                findByCountryIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(
                        request.getAddress().getCountry(),
                        request.getAddress().getCity(),
                        request.getAddress().getStreet());
        if (address.isEmpty()) {
            Address add = new Address();
            add = this.addressRepository.save(addressMapper.convertAddressResponseToAddress(request.getAddress()));
            supplier.setAddress(add);
        } else {
            supplier.setAddress(address.get());
        }
        return this.supplierRepository.save(supplier);
    }


    public SupplierResponse findSupplierById(UUID id) {
        Supplier supplier = this.supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        return this.supplierMapper.convertToResponse(supplier);
    }

    public SupplierResponse deleteSupplierById(UUID id) {
        Supplier supplier = this.supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        this.supplierRepository.deleteById(supplier.getId());
        return this.supplierMapper.convertToResponse(supplier);
    }

    public SupplierResponse changeAddress(UUID id, Address address) {
        Supplier supplier = this.supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        Address updatedAddress = new Address();
        Optional<Address> optionalAddress = this.addressRepository
                .findByCountryIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(
                        address.getCountry(),
                        address.getCity(),
                        address.getStreet()
                );
        updatedAddress = optionalAddress.orElseGet(() -> this.addressRepository.save(address));
        supplier.setAddress(updatedAddress);
        supplier = this.supplierRepository.save(supplier);
        return this.supplierMapper.convertToResponse(supplier);
    }

    public List<SupplierResponse> getAllSuppliers(Integer limit, Integer offset) {
        if (limit == null && offset == null) {
            return this.supplierRepository
                    .findAll()
                    .stream()
                    .map(this.supplierMapper::convertToResponse)
                    .collect(Collectors.toList());
        }

        if (limit != null && limit <= 0) {
            throw new BadRequestException("limit must be greater than 0");
        }

        if (offset != null && offset < 0) {
            throw new BadRequestException("offset cannot be negative");
        }
        Integer pageSize = (limit != null) ? limit : Integer.MAX_VALUE;
        Integer pageNum = (limit != null && offset != null) ? offset / limit : 0;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Supplier> page = this.supplierRepository.findAll(pageRequest);
        return page
                .stream()
                .map(this.supplierMapper::convertToResponse)
                .collect(Collectors.toList());
    }
}
