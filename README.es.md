# Java Library — contexto del proyecto

Aplicación histórica de aprendizaje con Java 8, Spring Boot, Spring MVC, Thymeleaf, JPA y MySQL. El código incluye libros, autores, editoriales, registro de usuarios y login mediante formularios.

Se conserva como antecedente técnico. No se presenta como sistema productivo ni se atribuye autoría individual sin evidencia de commits.

El [README principal](README.md) describe la estructura, los requisitos y la ejecución. El [caso de seguridad](docs/SECURITY_HARDENING.md) documenta los permisos, CSRF y las verificaciones.

**Verificación del 16 de noviembre de 2026:** 71 pruebas HTTP MockMvc aprobadas con JDK 11.0.17 y Maven 3.9.0. Se probaron filtros de seguridad y plantillas reales con servicios/repositorios simulados. No se ejecutaron MySQL, el test completo de contexto, navegador ni despliegue.

Los borrados de libros, autores y editoriales ahora requieren POST, rol ADMIN y CSRF. El catálogo sigue disponible en modo lectura; los usuarios registrados mantienen rol USER. No se agregaron administradores ni credenciales por defecto.

La validación de cantidades, las migraciones, la auditoría de dependencias/historial y las pruebas de persistencia siguen pendientes.
