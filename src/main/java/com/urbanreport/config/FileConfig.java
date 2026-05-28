package com.urbanreport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.io.File;

@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
        System.out.println(">>> Serving uploads from: " + uploadPath);
    }
}