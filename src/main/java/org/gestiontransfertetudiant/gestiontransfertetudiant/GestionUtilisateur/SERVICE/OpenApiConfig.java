package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gestion Transfert Étudiant API",
                version = "1.0",
                description = "API REST documentée avec Springdoc OpenAPI",
                contact = @Contact(name = "Gestion Transfert Étudiant", email = "support@gestiontransfertetudiant.local"),
                license = @License(name = "Apache 2.0")
        )
)
public class OpenApiConfig {
}
