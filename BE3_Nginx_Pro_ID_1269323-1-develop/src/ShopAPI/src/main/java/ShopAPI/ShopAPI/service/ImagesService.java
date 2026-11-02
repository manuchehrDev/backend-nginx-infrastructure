package ShopAPI.ShopAPI.service;

import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageResponse;
import ShopAPI.ShopAPI.entity.Images;
import ShopAPI.ShopAPI.entity.Product;
import ShopAPI.ShopAPI.exception.BadRequestException;
import ShopAPI.ShopAPI.exception.InvalidImageFormatException;
import ShopAPI.ShopAPI.exception.ResourceNotFoundException;
import ShopAPI.ShopAPI.mapper.ImageMapper;
import ShopAPI.ShopAPI.repository.ImagesRepository;
import ShopAPI.ShopAPI.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImagesService {
    @Autowired
    private ImagesRepository imagesRepository;
    @Autowired
    private ProductRepository productRepository;

    private final ImageMapper imageMapper = new ImageMapper();

    public ImageResponse getImageById(UUID id) {
        Images images = this.imagesRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Image not found"));
        return this.imageMapper.convertToResponse(images, "SUCCESS!");
    }

    public ImageResponse getImageByIdProduct(UUID id) {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Optional.of(product.getImageId()).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        return this.imageMapper.convertToResponse(product.getImageId(), "SUCCESS");
    }

    public ImageResponse deleteImageById(UUID id) {
        Images Images = this.imagesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        this.imagesRepository.deleteById(id);
        return this.imageMapper.convertToResponse(Images, "IMAGE IS DELETED");
    }

    public Images replaceImage(UUID id, MultipartFile file) throws IOException {
        Images images = this.imagesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        images.setImage(file.getBytes());
        return this.imagesRepository.saveAndFlush(images);
    }

    public Images saveImage(UUID id, MultipartFile file) throws IOException {

        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Товар с ID " + id + " не найден"
                ));
        if (product.getImageId() != null) {
            throw new BadRequestException(
                    "У товара уже есть изображение. Используйте PUT /images/{id} для обновления"
            );
        }

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Файл изображения не может быть пустым");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidImageFormatException(
                    "Файл должен быть изображением (JPEG, PNG, GIF и т.д.)"
            );
        }
        Images images = new Images();
        images.setImage(file.getBytes());
        Images savedImage = this.imagesRepository.save(images);
        product.setImageId(savedImage);
        this.productRepository.save(product);
        return images;
    }
}
