package com.hunter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@PropertySource("classpath:auth_mapper.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment environment;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;


	@Bean
	protected AuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.headers().frameOptions().disable();

		processAuthorization(http)
		.and()
			.formLogin()
			.loginPage("/login")
			.usernameParameter("phone")
			.permitAll()
		.and()
			.logout()
			.logoutUrl("/logout")
			.permitAll()
		.and()
			.exceptionHandling().accessDeniedPage("/accessDenied");

	}

	private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry processAuthorization(HttpSecurity http) throws Exception {

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();

		String[] allPermitUrls = environment.getProperty("all-permit-urls").split("~");
		String[] authPermitUrls = environment.getProperty("authentication-based-urls").split("~");
		String[] restrictedUrls = environment.getProperty("authority-based-urls").split("~");

		for (String restrictedUrl : restrictedUrls) {
			
			String authority = environment.getProperty(restrictedUrl);
			if(authority == null) continue;
			
			urlRegistry.antMatchers("/" + restrictedUrl + "/**").permitAll(); // .hasAuthority(authority);
			
		}

		for (String auth : authPermitUrls) {
			urlRegistry.antMatchers("/" + auth).permitAll(); //.authenticated();
		}
		
		for (String all : allPermitUrls) {
			urlRegistry.antMatchers("/" + all).permitAll();
		}
		
		return urlRegistry.anyRequest().permitAll(); //.authenticated();
	}

}
