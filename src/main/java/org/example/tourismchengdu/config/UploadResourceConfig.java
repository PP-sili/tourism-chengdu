package org.example.tourismchengdu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    public UploadResourceConfig(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String urlPrefix = uploadProperties.getUrlPrefix();
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }

        Path basePath = Paths.get(uploadProperties.getBaseDir()).toAbsolutePath().normalize();
        String location = basePath.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        registry.addResourceHandler(urlPrefix + "**")
                .addResourceLocations(location);
    }
}
