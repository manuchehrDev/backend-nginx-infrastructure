package ShopAPI.ShopAPI.controller;


import ShopAPI.ShopAPI.dto.request.addressRequest.AddressRequest;
import ShopAPI.ShopAPI.dto.request.supplierRequest.SupplierRequest;
import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import ShopAPI.ShopAPI.dto.response.errorResponse.ErrorResponse;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import ShopAPI.ShopAPI.entity.Supplier;
import ShopAPI.ShopAPI.mapper.AddressMapper;
import ShopAPI.ShopAPI.mapper.SupplierMapper;
import ShopAPI.ShopAPI.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.Internal;
import org.hibernate.id.IntegralDataTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Поставщики", description = "Управление поставщиками товаров")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping()
    @Operation(
            summary = "Добавление нового поставщика",
            description = "Создаёт нового поставщика с указанными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Поставщик успешно создан",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Поставщик с таким названием уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<SupplierResponse> addSuppliers(
            @Parameter(
                    description = "Данные для создания поставщика",
                    required = true
            )
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new SupplierMapper().convertToResponse(this.supplierService.saveSupplier(request)));
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Получение поставщика по ID",
            description = "Возвращает полную информацию о поставщике по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Поставщик найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Поставщик не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<SupplierResponse> getSupplierById(
            @Parameter(
                    description = "UUID поставщика",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @Valid @PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.supplierService.findSupplierById(id));
    }

    @GetMapping()
    @Operation(
            summary = "Получение всех поставщиков",
            description = "Возвращает список всех поставщиков с поддержкой пагинации. " +
                    "Если параметры не указаны, возвращает всех поставщиков."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список поставщиков успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SupplierResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры пагинации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<?> getAllSuppliers(
            @Parameter(
                    description = "Количество записей на странице",
                    example = "10"
            )
            @RequestParam(name = "limit", required = false) Integer limit,
            @Parameter(
                    description = "Сдвиг (сколько записей пропустить)",
                    example = "0"
            ) @RequestParam(name = "offset", required = false) Integer offset) {
        return ResponseEntity.status(HttpStatus.OK).body(this.supplierService.getAllSuppliers(limit, offset));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление поставщика",
            description = "Удаляет поставщика по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Поставщик успешно удалён",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Поставщик не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Невозможно удалить поставщика, у которого есть товары",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteSupplierById(
            @Parameter(
                    description = "UUID поставщика для удаления",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable("id") UUID id) {
        this.supplierService.deleteSupplierById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Изменение адреса поставщика",
            description = "Обновляет адрес существующего поставщика по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Адрес успешно обновлён",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SupplierResponse.class)
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
    @PatchMapping("/{id}/address")
    public ResponseEntity<SupplierResponse> changeSupplierAddress(
            @Parameter(
                    description = "UUID поставщика",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @Valid @PathVariable("id") UUID id,
            @Parameter(
                    description = "Новый адрес поставщика",
                    required = true
            )
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.supplierService.changeAddress(id, new AddressMapper().convertAddressForService(request)));
    }

}
