package it.desossi.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
		/**
		 * When should you use CSRF protection? 
		 * Our recommendation is to use CSRF protection for any request that could be processed by a browser by normal users. 
		 * If you are creating a service that is used only by non-browser clients, you likely want to DISABLE CSRF protection.
		 * (from https://docs.spring.io/spring-security/reference/features/exploits/csrf.html#csrf-when)
		 */
		serverHttpSecurity.csrf()
			.disable()//disabilitiamo csrf perchè usiamo Postman come client, cioè non faremo chiamate da browser
			.authorizeExchange(exchange -> exchange
						.pathMatchers("/eureka/**")//path verso le risorse statiche di Eureka
						.permitAll()//permettiamo tutte le chiamate alle risorse statiche di Eureka
						.anyExchange()//ma ogni altra chiamata...
						.authenticated())//...deve essere autenticata
			.oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);//e abilitiamo le capacità di resource server (OAuth2), che saranno di tipo JWT
		return serverHttpSecurity.build();
	}

}
