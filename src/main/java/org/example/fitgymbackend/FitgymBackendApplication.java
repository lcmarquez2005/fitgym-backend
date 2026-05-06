//Enciende TODO el framework

package org.example.fitgymbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FitgymBackendApplication {

    public static void main(String[] args) {
        // Cargar variables del .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load(); //Lee el archivo .env

        //Hace disponibles las variables para application.yaml
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("FRONTEND_URL", dotenv.get("FRONTEND_URL", "http://localhost:5173,http://localhost:5174"));

        SpringApplication.run(FitgymBackendApplication.class, args); //Arranca Tomcat (servidor web), escanea componentes, conecta BD
    }
}