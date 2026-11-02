package ShopAPI.ShopAPI.service;

import ShopAPI.ShopAPI.dto.request.productRequest.StockRequest;
import ShopAPI.ShopAPI.dto.response.productResponse.ProductInfoResponse;
import ShopAPI.ShopAPI.dto.response.productResponse.ProductResponse;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import ShopAPI.ShopAPI.entity.Product;
import ShopAPI.ShopAPI.entity.Supplier;
import ShopAPI.ShopAPI.exception.BadRequestException;
import ShopAPI.ShopAPI.exception.OperationNotExcepted;
import ShopAPI.ShopAPI.exception.ResourceNotFoundException;
import ShopAPI.ShopAPI.mapper.ProductMapper;
import ShopAPI.ShopAPI.repository.ProductRepository;
import ShopAPI.ShopAPI.repository.SupplierRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    private final ProductMapper productMapper = new ProductMapper();

    public Product create(Product product) {
        Supplier supplier = this.supplierRepository
                .findById(product.getSupplierId().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        product.setSupplierId(supplier);
        return this.productRepository.save(product);
    }

    public Product getProductById(UUID id) {
        return this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product deleteProduct(UUID id) {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        this.productRepository.deleteById(id);
        return product;
    }

    public Product changeStock(StockRequest stockRequest) {
        Product product = this.productRepository.findById(stockRequest.getId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getAvailableStock() < stockRequest.getQuantity()) {
            throw new BadRequestException("The request was not fulfilled. Either the PRODUCT or quantity was NOT FOUND, or the STOCK IS INCORRECT.");
        }
        product.setAvailableStock(product.getAvailableStock() - stockRequest.getQuantity());
        product.setLastUpdateDate(LocalDate.now());
        product = this.productRepository.saveAndFlush(product);
        return product;
    }

    public List<ProductInfoResponse> getAllProducts(Integer limit, Integer offset) {
        if (limit == null && offset == null) {
            return this.productRepository
                    .findAll()
                    .stream()
                    .map(productMapper::convertToInfoResponse)
                    .collect(Collectors.toList());
        }
        if (limit != null && limit <= 0) {
            throw new BadRequestException("limit must be greater than 0");
        }

        if (offset != null && offset < 0) {
            throw new BadRequestException("offset cannot be negative");
        }
        int pageSize = (limit != null) ? limit : Integer.MAX_VALUE;
        int pageNum = (offset != null && limit != null) ? offset / limit : 0;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Product> products = this.productRepository.findAll(pageRequest);
        return products.stream().map(productMapper::convertToInfoResponse).collect(Collectors.toList());
    }
}
