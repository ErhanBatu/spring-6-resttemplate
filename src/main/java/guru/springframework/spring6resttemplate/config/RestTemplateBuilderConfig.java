package guru.springframework.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

    //if you put inside value any text without ${} it will directly inject that
    //but with this ${} it will go to application.properties and will retreive rest.template.rootUrl
    @Value("${rest.template.rootUrl}")
    String rootUrl;
    @Value("${rest.template.username}")
    String username;
    @Value("${rest.template.password}")
    String password;

    //to be able to take the values above you have to define this bean below
    @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer){

        return configurer.configure(new RestTemplateBuilder())
                .basicAuthentication(username, password)
                .uriTemplateHandler(new DefaultUriBuilderFactory(rootUrl));

    }


}
