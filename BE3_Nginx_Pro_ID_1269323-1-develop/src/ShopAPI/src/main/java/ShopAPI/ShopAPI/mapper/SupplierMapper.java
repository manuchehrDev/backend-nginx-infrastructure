package ShopAPI.ShopAPI.mapper;

import ShopAPI.ShopAPI.dto.request.supplierRequest.SupplierRequest;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import ShopAPI.ShopAPI.entity.Supplier;

public class SupplierMapper {
    public SupplierResponse convertToResponse(Supplier supplier){
        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setId(supplier.getId());
        supplierResponse.setName(supplier.getName());
        supplierResponse.setPhoneNumber(supplier.getPhoneNumber());
        supplierResponse.setAddress(new AddressMapper().convertToResponse( supplier.getAddress()));
        return supplierResponse;
    }


    public Supplier convertToService(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setAddress(new AddressMapper().convertAddressResponseToAddress(request.getAddress()));
        supplier.setName(request.getName());
        supplier.setPhoneNumber(request.getPhoneNumber());
        return supplier;
    }
}
