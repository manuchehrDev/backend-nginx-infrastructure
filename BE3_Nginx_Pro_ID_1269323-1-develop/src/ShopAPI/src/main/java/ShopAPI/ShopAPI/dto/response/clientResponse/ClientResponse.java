package ShopAPI.ShopAPI.dto.response.clientResponse;

import ShopAPI.ShopAPI.dto.request.clientReauest.CreateClientRequest;
import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными клиента")
public class ClientResponse {

    @Schema(description = "Уникальный идентификатор клиента", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Имя клиента", example = "Иван")
    private String clientName;

    @Schema(description = "Фамилия клиента", example = "Петров")
    private String clientSurname;

    @Schema(description = "Дата рождения", example = "1990-05-15")
    private LocalDate birthday;

    @Schema(description = "Пол (M - мужской, F - женский)", example = "M", allowableValues = {"M", "F"})
    private char gender;

    @Schema(description = "Дата регистрации", example = "2026-07-30T15:30:00")
    private LocalDateTime registrationDate;

    @Schema(description = "Адрес клиента")
    private AddressResponse address;

    public ClientResponse(){

    }

    public ClientResponse(CreateClientRequest request) {
        this.id = request.getId();
        this.clientName = request.getClientName();
        this.clientSurname = request.getClientSurname();
        this.birthday = request.getBirthday();
        this.gender = this.getGender();
        this.registrationDate = request.getRegistrationDate();
        this.address = new AddressResponse(request.getAddress());
    }

}
