/*
*SecurityConfig.java: Se encarga de la identidad y permisos (¿Quién eres? ¿Tienes permiso de ver esto?). Es parte de la capa de seguridad.
*/
package org.example.fitgymbackend.config;

import org.example.fitgymbackend.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //informa a springboot de que esta clase es importante para configuraciones
@EnableWebSecurity //Activa Spring Security. Sin esto, todos los endpoints estarían abiertos al mundo.
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter; //tipo de dato personalizado


    //Inyección de Dependencias por Constructor: Cuando Spring crea la clase SecurityConfig, busca si ya tiene guardado un JwtRequestFilter. Si lo tiene, te lo "pasa" o "inyecta" automáticamente.
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    } //Guarda ese filtro que Spring te dio en la variable que declaraste arriba para que puedas usarlo después en la configuración.

    @Bean //Le indica a Spring que el objeto que devuelve ese método debe ser guardado en el "contenedor de Spring" para que otras partes del código puedan usarlo
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //Tipo de Filtro de seguridad que desactivamos para poder hacer eticiones sin tokens adicionales de formulario desde el frontend
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Para no guardar sesiones en memoria, y cada petición debe traer su propio token
                .authorizeHttpRequests(auth -> auth
                        // 👇 ENDPOINTS PÚBLICOS (NO requieren token PARA PRUEBAS)
                        .requestMatchers(
                                "/api/auth/**",           // Login, registro, reset password
                                "/api/users/upload-photo", // Subir fotos
                                "/api/users",             // 👈 GET y POST de usuarios (temporal)
                                "/api/users/**",          // 👈 Cualquier subruta de users (temporal)
                                "/uploads/**"             // Archivos estáticos
                        ).permitAll()
                        // 👇 CUALQUIER OTRA COSA requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); //Antes de pedirle usuario y contraseña, revisa si ya trae un Token válido en la mano

        return http.build();
    }

    @Bean  //Anotacion para hacer global el siguiente metodo y se use en otras partes del código
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //Codificador de contraseñas
}