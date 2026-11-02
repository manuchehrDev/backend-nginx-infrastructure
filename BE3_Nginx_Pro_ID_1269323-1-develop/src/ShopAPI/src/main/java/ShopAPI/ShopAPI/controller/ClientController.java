package ShopAPI.ShopAPI.controller;


import ShopAPI.ShopAPI.dto.request.addressRequest.AddressRequest;
import ShopAPI.ShopAPI.dto.request.clientReauest.CreateClientRequest;
import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import ShopAPI.ShopAPI.dto.response.clientResponse.ClientDeleteResponse;
import ShopAPI.ShopAPI.dto.response.clientResponse.ClientResponse;
import ShopAPI.ShopAPI.dto.response.errorResponse.ErrorResponse;
import ShopAPI.ShopAPI.entity.Address;
import ShopAPI.ShopAPI.entity.Client;
import ShopAPI.ShopAPI.mapper.AddressMapper;
import ShopAPI.ShopAPI.mapper.ClientMapper;
import ShopAPI.ShopAPI.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//POST   /api/v1/clients
//DELETE /api/v1/clients/{id}
//GET    /api/v1/clients/search
//GET    /api/v1/clients
//PATCH  /api/v1/clients/{id}/address

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Клиенты", description = "Управление клиентами")
public class ClientController {

    @Autowired
    private ClientService clientService;

    private ClientMapper clientMapper;

    @GetMapping()
    @Operation(summary = "Получение всех клиентов с пагинацией")
    public ResponseEntity<List<ClientResponse>> getAllClients(
            @Parameter(description = "Количество записей") @RequestParam(value = "limit", required = false) Integer limit,
            @Parameter(description = "Сдвиг (пропустить N записей)") @RequestParam(value = "offset", required = false) Integer offset) {
        return ResponseEntity.status(HttpStatus.OK).body(this.clientService.getAllClients(limit, offset));
    }

    @PostMapping("")
    @Operation(summary = "Добавление нового клиента",
            description = "Создаёт клиента с указанными данными")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "409", description = "Клиент уже существует")
    })
    public ResponseEntity<ClientResponse> addClient(@Valid @RequestBody CreateClientRequest clientRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClientMapper().convertForResponse(this.clientService.saveClient(clientRequest)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент удалён"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    public ResponseEntity<Void> getDeleteById(@PathVariable("id") UUID uuid) {
        this.clientService.deleteClientByUUID(uuid);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    @Operation(
            summary = "Поиск клиентов по имени и фамилии",
            description = "Возвращает список клиентов, соответствующих указанным имени и фамилии"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный поиск (возвращает список, даже если пустой)",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<ClientResponse>> searchClient(
            @Parameter(
                    description = "Имя клиента для поиска",
                    required = true,
                    example = "Иван"
            )
            @Valid @RequestParam(value = "name") String name,

            @Parameter(
                    description = "Фамилия клиента для поиска",
                    required = true,
                    example = "Петров"
            )
            @Valid @RequestParam(value = "surname") String surname) {
        return ResponseEntity.status(HttpStatus.OK).body(this.clientService.searchByNameAndSurname(name, surname));
    }

    @PatchMapping("/{id}/address")
    @Operation(
            summary = "Изменение адреса клиента",
            description = "Обновляет адрес существующего клиента по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Адрес успешно обновлён",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Клиент не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ClientResponse> changeAddress(
            @Parameter(
                    description = "UUID клиента",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @Valid @PathVariable("id") UUID id,

            @Parameter(
                    description = "Новый адрес клиента",
                    required = true
            )
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new ClientMapper().convertForResponse(this.clientService.changeAddress(id, new AddressMapper().convertAddressForService(request))));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Получение клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    public ResponseEntity<ClientResponse> getById(@Valid @PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ClientMapper().convertForResponse(this.clientService.getClientById(id)));
    }


}
