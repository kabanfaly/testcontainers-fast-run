package com.geekshow.testcontainersfastrun;

import com.geekshow.testcontainersfastrun.handler.ContactHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
public class RoutesConfig {

  @Bean
  RouterFunction<?> routerFunction(ContactHandler contactHandler) {
    return RouterFunctions.route()
        .POST("/api/contacts", contactHandler::saveContact)
        .GET("/api/contacts", contactHandler::getAllContacts)
        .GET("/api/contacts/{id}", contactHandler::getContactById)
        .build();
  }
}
