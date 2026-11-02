package ShopAPI.ShopAPI.controller;


import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageInfoResponse;
import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageResponse;
import ShopAPI.ShopAPI.dto.response.errorResponse.ErrorResponse;
import ShopAPI.ShopAPI.entity.Images;
import ShopAPI.ShopAPI.mapper.ImageMapper;
import ShopAPI.ShopAPI.service.ImagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

//| | POST | `/api/v1/products/{productId}/image` | Добавление изображения |
//| | PUT | `/api/v1/images/{imageId}` | Изменение изображения |
//| | DELETE | `/api/v1/images/{imageId}` | Удаление изображения |
//| | GET | `/api/v1/products/{productId}/image` | Получение по ID товара |
//| | GET | `/api/v1/images/{imageId}` | Получение по ID изображения |

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Изображения", description = "Управление изображениями товаров")
public class ImagesController {


    @Autowired
    private ImagesService imagesService;

    @PostMapping(value = "/products/{productId}/image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Добавление изображения к товару",
            description = "Загружает новое изображение и привязывает его к указанному товару"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Изображение успешно добавлено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации или неверный формат файла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар с указанным ID не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ImageResponse> additionImage(
            @Parameter(
                    description = "UUID товара",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @Valid @PathVariable("productId") UUID id,

            @Parameter(
                    description = "Файл изображения (форматы: JPEG, PNG, GIF)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ImageMapper().convertToResponse(this.imagesService.saveImage(id, file), "SUCCESS!!!"));
    }


    @PutMapping(value = "/images/{imageId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Изменение изображения",
            description = "Заменяет существующее изображение на новое"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно обновлено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации или неверный формат файла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Изображение с указанным ID не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ImageResponse> changeImage(
            @Parameter(
                    description = "UUID изображения для замены",
                    required = true,
                    example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
            )
            @Valid @PathVariable("imageId") UUID id,
            @Parameter(
                    description = "Новый файл изображения",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
           @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(new ImageMapper().convertToResponse(this.imagesService.replaceImage(id, file), "SUCCESS!!!"));
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(
            summary = "Удаление изображения",
            description = "Удаляет изображение по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно удалено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageInfoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Изображение с указанным ID не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ImageInfoResponse> deleteImage(
            @Parameter(
                    description = "UUID изображения для удаления",
                    required = true,
                    example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
            )
            @Valid @PathVariable("imageId") UUID id) {
        ImageResponse imageResponse = this.imagesService.deleteImageById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ImageMapper().convertToMessageResponse(imageResponse.getId(), imageResponse.getMessage()));
    }


    @GetMapping("/products/{productId}/image")
    @Operation(
            summary = "Получение изображения по ID товара",
            description = "Возвращает изображение, привязанное к указанному товару, для скачивания"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно получено",
                    content = @Content(
                            mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден или у товара нет изображения",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<byte[]> getImagesByIdProduct(
            @Parameter(
                    description = "UUID товара",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable("productId") UUID id) {
        ImageResponse imageResponse = this.imagesService.getImageByIdProduct(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"product-image.jpg\"")
                .body(imageResponse.getImage());
    }


    @GetMapping("/images/{imageId}")
    @Operation(
            summary = "Получение изображения по ID",
            description = "Возвращает изображение по его UUID для скачивания"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно получено",
                    content = @Content(
                            mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Изображение с указанным ID не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<byte[]> getImagesByIdImages(
            @Parameter(
                    description = "UUID изображения",
                    required = true,
                    example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable("imageId") UUID id) {
        ImageResponse imageResponse = this.imagesService.getImageById(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"image.jpg\"")
                .body(imageResponse.getImage());

    }
}
