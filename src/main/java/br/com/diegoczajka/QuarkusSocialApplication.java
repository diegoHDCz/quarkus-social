package br.com.diegoczajka;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "API QUARKUS SOCIAL",
                version = "1.0",
                contact = @Contact(
                        name = "Diego Czajka",
                        url = "https://my-portfolio-psi-five.vercel.app/",
                        email = "diego.czajka@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/license/LICENSE-2.0.html"
                )
        )
)
public class QuarkusSocialApplication extends Application {
}
