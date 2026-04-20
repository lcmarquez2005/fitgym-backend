# ☁️ Infraestructura y Arquitectura Cloud (AWS)

Para el soporte de datos y la persistencia del backend de **FitGym**, hemos implementado una infraestructura en la nube utilizando **Amazon Web Services (AWS)**. La configuración se centra en la seguridad, la colaboración en equipo y la optimización de costos.

## 🗄️ Base de Datos Relacional (RDS)
* **Motor:** MySQL Community Edition 8.0.
* **Instancia:** `database-fitgym` (Capa gratuita / db.t3.micro).
* **Configuración de Red:**
    * **Accesibilidad Pública:** Habilitada para permitir la conexión desde los entornos de desarrollo locales de todos los integrantes del equipo.
    * **Security Groups:** Se configuraron reglas de entrada (Inbound Rules) para el puerto `3306` con origen `0.0.0.0/0`, permitiendo el acceso remoto global.
    * **SSL:** Conexión cifrada obligatoria mediante el certificado `global-bundle.pem`.

## 🔐 Gestión de Identidades y Accesos (IAM)
Para permitir que los 5 integrantes del equipo gestionen la base de datos sin comprometer la cuenta raíz, se implementó:
* **Usuarios IAM:** Cuentas individuales para cada desarrollador con acceso a la Consola de Administración.
* **Políticas de Privilegio Mínimo:** Los usuarios tienen permisos restringidos exclusivamente para:
    * `rds:DescribeDBInstances`: Visualizar el estado de la base de datos.
    * `rds:StartDBInstance`: Encender la instancia para pruebas.
    * `rds:StopDBInstance`: Apagar la instancia al terminar la jornada.

## 💰 Automatización de Costos (DevOps / Serverless)
Dado que los créditos de AWS son limitados, se diseñó un sistema de **Auto-Apagado** para evitar que la instancia se quede encendida por descuidos:

1. **AWS Lambda:** Función en Python utilizando la librería `boto3` que inspecciona el estado de la RDS y ejecuta el comando de apagado si detecta que la instancia está activa.
2. **Amazon EventBridge (Scheduler):** Temporizador configurado con una expresión Cron (`0 1 * * ? *`).
    * **Horario de ejecución:** 01:00 AM (Zona Horaria: America/Mexico_City).
    * **Acción:** Dispara la función Lambda diariamente para garantizar que la infraestructura esté inactiva durante la madrugada.

## 🚀 Conexión con Spring Boot
El backend se conecta a la instancia de RDS mediante las siguientes tecnologías:
* **Spring Data JPA & Hibernate:** Para el mapeo objeto-relacional y la creación automática de tablas (`ddl-auto: update`).
* **Variables de Entorno (.env):** Gestión segura de credenciales (Host, User, Password) para evitar la exposición de datos sensibles en el repositorio de Git.
* **Lombok:** Para reducir el código boilerplate en las entidades de persistencia.

---
*Nota: Cada desarrollador debe configurar su archivo `.env` local con el endpoint proporcionado en la consola de AWS y contar con sus credenciales de IAM activas.*