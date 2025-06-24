package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dgwkty2dw",
                "api_key", "164849843772972",
                "api_secret", "uS-CgmD5cEGrocWd8ISnCGENwKo",
                "secure", true
        ));
    }
}