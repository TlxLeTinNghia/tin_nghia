package com.example.tin_nghia.application.service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

final class UploadedImage {
    private static final int MAX_BYTES = 1024 * 1024;
    private static final int MAX_DIMENSION = 4096;

    private UploadedImage() {}

    static String contentType(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Bạn chưa chọn tệp ảnh.");
        }
        if (data.length > MAX_BYTES) {
            throw new IllegalArgumentException("Ảnh phải từ 1 MB trở xuống.");
        }

        try (var input = new MemoryCacheImageInputStream(new ByteArrayInputStream(data))) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("Chỉ hỗ trợ ảnh JPEG hoặc PNG hợp lệ.");
            }

            ImageReader reader = readers.next();
            try {
                String mime = switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                    case "jpeg" -> "image/jpeg";
                    case "png" -> "image/png";
                    default -> throw new IllegalArgumentException("Chỉ hỗ trợ ảnh JPEG hoặc PNG.");
                };

                reader.setInput(input, true, true);
                if (reader.getWidth(0) > MAX_DIMENSION || reader.getHeight(0) > MAX_DIMENSION) {
                    throw new IllegalArgumentException("Chiều rộng và chiều cao ảnh tối đa là 4096 px.");
                }
                reader.read(0);
                return mime;
            } finally {
                reader.dispose();
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Không đọc được ảnh JPEG hoặc PNG.", ex);
        }
    }
}
