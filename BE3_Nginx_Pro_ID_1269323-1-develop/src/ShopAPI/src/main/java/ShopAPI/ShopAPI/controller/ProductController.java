package ShopAPI.ShopAPI.controller;


import ShopAPI.ShopAPI.dto.request.productRequest.ProductRequest;
import ShopAPI.ShopAPI.dto.request.productRequest.ProductStockRequest;
import ShopAPI.ShopAPI.dto.request.productRequest.StockRequest;
import ShopAPI.ShopAPI.dto.response.errorResponse.ErrorResponse;
import ShopAPI.ShopAPI.dto.response.productResponse.ProductInfoResponse;
import ShopAPI.ShopAPI.dto.response.productResponse.ProductResponse;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import ShopAPI.ShopAPI.entity.Product;
import ShopAPI.ShopAPI.mapper.ProductMapper;
import ShopAPI.ShopAPI.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//
// | PATCH | `/api/v1/products/{id}/stock` | Уменьшение количества |
//        | | GET | `/api/v1/products/{id}` | Получение по ID |
//        | | GET | `/api/v1/products?limit=&offset=` | Получение всех (с пагинацией) |
//        | | DELETE | `/api/v1/products/{id}` | Удаление товара |
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Товары", description = "Управление товарами в магазине")
public class ProductController {

    @Autowired
    private ProductService productService;

    private final ProductMapper productMapper = new ProductMapper();


    @GetMapping()
    @Operation(
            summary = "Получение всех товаров",
            description = "Возвращает список всех товаров с поддержкой пагинации. " +
                    "Если параметры не указаны, возвращает все товары."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список товаров успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductInfoResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры пагинации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<ProductInfoResponse>> getAllStockProducts(
            @Parameter(
                    description = "Количество записей на странице",
                    example = "10"
            )
            @RequestParam(value = "limit", required = false) Integer limit,
            @Parameter(
                    description = "Сдвиг (сколько записей пропустить)",
                    example = "0"
            )
            @RequestParam(value = "offset", required = false) Integer offset) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.getAllProducts(limit, offset));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение товара по ID",
            description = "Возвращает полную информацию о товаре по его уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ProductInfoResponse> getProductById(
            @Parameter(
                    description = "UUID товара",
                    required = true,
                    example = "987fcdeb-51a2-43d7-9e56-426614174000"
            )
            @Valid @PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productMapper.convertToInfoResponse(this.productService.getProductById(id)));
    }

    @PostMapping("")
    @Operation(
            summary = "Создание нового товара",
            description = "Добавляет новый товар в каталог магазина"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Товар успешно создан",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Поставщик не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ProductResponse> create(
            @Parameter(
                    description = "Данные для создания товара",
                    required = true
            )
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productMapper.convertToResponse(this.productService.create(this.productMapper.convertToService(request))));
    }

    @PatchMapping("/{id}/stock")
    @Operation(
            summary = "Уменьшение количества товара на складе",
            description = "Уменьшает доступное количество товара на указанное значение"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Количество успешно обновлено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации или недостаточно товара на складе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ProductResponse> stock(
            @Parameter(
                    description = "UUID товара",
                    required = true,
                    example = "987fcdeb-51a2-43d7-9e56-426614174000"
            )
            @PathVariable("id") UUID id,
            @Parameter(
                    description = "Данные для уменьшения количества",
                    required = true
            )
            @Valid @RequestBody ProductStockRequest productStockRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                this.productMapper.convertToResponse(
                        this.productService.changeStock(
                                new StockRequest(id, productStockRequest.getQuantity()))));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление товара",
            description = "Удаляет товар из каталога по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар успешно удалён",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteProductById(
            @Parameter(
                    description = "UUID товара для удаления",
                    required = true,
                    example = "987fcdeb-51a2-43d7-9e56-426614174000"
            )
            @Valid @PathVariable("id") UUID id) {
        this.productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}
