package ShopAPI.ShopAPI.mapper;

import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageInfoResponse;
import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageResponse;
import ShopAPI.ShopAPI.entity.Images;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.UUID;

public class ImageMapper {
    public ImageResponse convertToResponse(Images image, String message) {
        ImageResponse response = new ImageResponse();
        response.setId(image.getId());
        response.setMessage(message);
        response.setImage(image.getImage());
        return response;
    }

    public ImageResponse convertToResponse(UUID id, String message) {
        ImageResponse response = new ImageResponse();
        response.setId(id);
        response.setMessage(message);
        return response;
    }

    public ImageInfoResponse convertToMessageResponse(UUID id, String message) {
        ImageInfoResponse imageInfoResponse = new ImageInfoResponse();
        imageInfoResponse.setMessage(message);
        imageInfoResponse.setUuid(id);
        return imageInfoResponse;
    }

}
