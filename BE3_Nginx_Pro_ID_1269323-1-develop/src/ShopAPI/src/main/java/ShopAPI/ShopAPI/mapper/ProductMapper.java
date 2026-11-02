package ShopAPI.ShopAPI.mapper;

import ShopAPI.ShopAPI.dto.request.productRequest.ProductRequest;
import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageInfoResponse;
import ShopAPI.ShopAPI.dto.response.productResponse.ProductInfoResponse;
import ShopAPI.ShopAPI.dto.response.productResponse.ProductResponse;
import ShopAPI.ShopAPI.entity.Product;
import ShopAPI.ShopAPI.entity.Supplier;

import java.time.LocalDate;

public class ProductMapper {
    public Product convertToService(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setAvailableStock(request.getAvailableStock());
        product.setLastUpdateDate(LocalDate.now());
        Supplier supplier = new Supplier();
        supplier.setId(request.getSupplierId());
        product.setSupplierId(supplier);
        return product;
    }

    public ProductResponse convertToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setLastUpdateDate(product.getLastUpdateDate().atStartOfDay());
        productResponse.setCategory(product.getCategory());
        productResponse.setAvailableStock(product.getAvailableStock());
        productResponse.setSupplierId(product.getSupplierId().getId());
        return productResponse;
    }

    public ProductInfoResponse convertToInfoResponse(Product product) {
        ProductInfoResponse productInfoResponse = new ProductInfoResponse();
        productInfoResponse.setId(product.getId());
        productInfoResponse.setName(product.getName());
        productInfoResponse.setPrice(product.getPrice());
        productInfoResponse.setCategory(product.getCategory());
        productInfoResponse.setAvailableStock(product.getAvailableStock());
        productInfoResponse.setLastUpdateDate(product.getLastUpdateDate().atStartOfDay());
        productInfoResponse.setSupplier(new SupplierMapper().convertToResponse(product.getSupplierId()));
        ImageInfoResponse imageInfoResponse = new ImageInfoResponse();
        if (product.getImageId() == null) {
            imageInfoResponse.setMessage("У товара нет изображения");
        } else {
            imageInfoResponse.setUuid(product.getImageId().getId());
            imageInfoResponse.setMessage("Изображение найдено");
        }

        productInfoResponse.setImage(imageInfoResponse);
        return productInfoResponse;
    }
}
