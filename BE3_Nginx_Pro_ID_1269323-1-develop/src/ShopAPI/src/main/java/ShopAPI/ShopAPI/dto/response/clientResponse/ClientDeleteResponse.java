package ShopAPI.ShopAPI.dto.response.clientResponse;

import ShopAPI.ShopAPI.entity.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "ClientDeleteResponse",
        description = "Ответ с данными удалённого клиента"
)
public class ClientDeleteResponse {

    @Schema(
            description = "Уникальный идентификатор удалённого клиента",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID id;

    @Schema(
            description = "Имя удалённого клиента",
            example = "Иван"
    )
    private String clientName;

    @Schema(
            description = "Фамилия удалённого клиента",
            example = "Петров"
    )
    private String clientSurname;

    @Schema(
            description = "Сообщение о результате операции",
            example = "Клиент успешно удалён"
    )
    private String message;

    public ClientDeleteResponse(Client client) {
        this.id = client.getId();
        this.clientName = client.getClientName();
        this.clientSurname = client.getClientSurname();
        this.message = "Клиент с ID " + client.getId() + " успешно удалён";
    }
}