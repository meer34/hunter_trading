package com.hunter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hunter.data.controller.JwtAuthenticationTokenFilter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.hunter")
@PropertySource("classpath:auth_mapper.properties")
public class SpringSecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Configuration
	@Order(1)
	public static class RestApiSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired private JwtAuthenticationTokenFilter jwtauthFilter;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.csrf().disable()
			.antMatcher("/api/**")
			.authorizeRequests()
			.antMatchers("/api/authenticate").permitAll()
			.antMatchers("/api/**").authenticated()
			.and()
			.addFilterBefore(jwtauthFilter, UsernamePasswordAuthenticationFilter.class);

			http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}
	}

	@Configuration
	@Order(2)
	public static class LoginFormSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired private Environment environment;
		@Autowired private UserDetailsServiceImpl userDetailsService;


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

			processAuthorization(http, true)
			.and()
			.formLogin()
			.loginPage("/login")
			.usernameParameter("phone")
			.permitAll()
			.and()
			.logout()
			.logoutUrl("/logout")
			.permitAll();

		}

		private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry processAuthorization(HttpSecurity http, boolean enableAuth) throws Exception {

			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();
			
			if(!enableAuth) return urlRegistry.anyRequest().permitAll();
			
			String[] allPermitUrls = environment.getProperty("all-permit-urls").split("~");
			String[] authPermitUrls = environment.getProperty("authentication-based-urls").split("~");
			String[] restrictedUrls = environment.getProperty("authority-based-urls").split("~");

			urlRegistry.antMatchers("/rest-login").permitAll();
			urlRegistry.antMatchers("/get_*").authenticated();

			for (String restrictedUrl : restrictedUrls) {

				String authority = environment.getProperty(restrictedUrl);
				if(authority == null) continue;

				urlRegistry.antMatchers("/" + restrictedUrl + "/**").hasAuthority(authority);

			}

			for (String auth : authPermitUrls) {
				urlRegistry.antMatchers("/" + auth).authenticated();
			}

			for (String all : allPermitUrls) {
				urlRegistry.antMatchers("/" + all).permitAll();
			}

			return urlRegistry.anyRequest().authenticated();
		}

	}
	
}