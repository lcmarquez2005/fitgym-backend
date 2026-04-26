/*
* WebConfig.java: Se encarga de la infraestructura del protocolo HTTP (CORS, rutas de archivos, formateadores de fechas). Es parte de la capa de presentación/MVC.
*/
package org.example.fitgymbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //informa a springboot de que esta clase es importante para configuraciones
public class WebConfig {

    @Value("${app.frontend-url}") //Para traer un dato desde el .yml
    private String frontendUrl;

    @Bean //Le indica a Spring que el objeto que devuelve ese método debe ser guardado en el "contenedor de Spring" para que otras partes del código puedan usarlo
    public WebMvcConfigurer corsConfigurer() { //configuración de los CORS
        return new WebMvcConfigurer() {
            @Override //Anotación para sobreescribir el metodo que ya existe en la clase padre o en la interfaz que se esta usando
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") //Aplica esta regla a todas las rutas del servidor
                        .allowedOrigins(frontendUrl) //Permite peticiones desde el frontend (cambia en producción)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") //Define qué "verbos" HTTP están permitidos.
                        .allowedHeaders("*") //PErmite todos las opciones de headers
                        .allowCredentials(true); //Permite que las peticiones incluyan cookies o el encabezado de Autorización (muy importante para JWT).
            }

            @Override //Anotación para sobreescribir el metodo que ya existe en la clase padre o en la interfaz que se esta usando
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**") //Spring, Si alguien pide una URL que empiece con /uploads/, no busques un Controller, busca un archivo real
                        .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/"); //Le dice que busque en una carpeta llamada uploads dentro de tu proyecto.
            }
        };
    }
}