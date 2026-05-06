/*
 * WebConfig.java: Configura CORS global y recursos estáticos.
 *
 * IMPORTANTE: Se usa CorsFilter como @Bean en lugar de WebMvcConfigurer
 * porque el CorsFilter se ejecuta ANTES de Spring Security, garantizando
 * que los preflight OPTIONS siempre reciban la respuesta correcta.
 */
package org.example.fitgymbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.frontend-urls}")
    private String frontendUrlsRaw;

    /**
     * CorsFilter registrado como Bean de alta prioridad.
     * Intercepta ANTES que Spring Security → los preflight OPTIONS siempre pasan.
     */
    @Bean
    public CorsFilter corsFilter() {
        // Parsear la lista separada por comas del .env
        List<String> allowedOrigins = Arrays.asList(frontendUrlsRaw.split(","));

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache preflight 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica a TODAS las rutas

        return new CorsFilter(source);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }
}