package school.hei.sary.endpoint.rest.controller;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sary.service.ImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/blacks")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PutMapping("/{id}")
    public String uploadImage(@PathVariable String id, @RequestParam("image") MultipartFile image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image.getBytes()));

        BufferedImage blackAndWhiteImage = convertToBlackAndWhite(bufferedImage);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(blackAndWhiteImage, "jpg", byteArrayOutputStream);
        String transformedImageUrl = imageService.saveTransformedImage(id, byteArrayOutputStream.toByteArray());
        return transformedImageUrl;
    }

    @GetMapping("/{id}")
    public Map<String, String> getImageUrls(@PathVariable String id) {
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        String originalImageUrl = imageService.generatePresignedUrl(id, "original.jpg", expiration);
        String transformedImageUrl = imageService.generatePresignedUrl(id, "transformed.jpg", expiration);

        Map<String, String> urlsMap = new HashMap<>();
        urlsMap.put("originalImageUrl", originalImageUrl);
        urlsMap.put("transformedImageUrl", transformedImageUrl);

        return urlsMap;
    }
    private BufferedImage convertToBlackAndWhite(BufferedImage originalImage) {
        BufferedImage blackAndWhiteImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        blackAndWhiteImage.getGraphics().drawImage(originalImage, 0, 0, null);
        return blackAndWhiteImage;
    }
}
