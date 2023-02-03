package com.increff.pos.spring;

import com.increff.pos.model.auth.UserPrincipal;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.ValidationUtil;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger logger = Logger.getLogger(SecurityConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http//
                // Match only these URLs
                .requestMatchers()//
                .antMatchers("/api/**")//
                .antMatchers("/ui/**")//
                .and().authorizeRequests()//
                .antMatchers("/api/orders/**").hasAnyAuthority("supervisor","operator")
                .antMatchers("/api/reports/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers(HttpMethod.POST, "/api/inventory/barcode/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers(HttpMethod.POST, "/api/**").hasAuthority("supervisor")
                .antMatchers(HttpMethod.PUT, "/api/**").hasAuthority("supervisor")
                .antMatchers("/api/admin/**").hasAuthority("supervisor")//
                .antMatchers("/api/**").hasAnyAuthority("supervisor", "operator")//
                .antMatchers("/ui/admin/**").hasAuthority("supervisor")//
                .antMatchers("/ui/**").hasAnyAuthority("supervisor", "operator")//
                // Ignore CSRF and CORS
                .and().csrf().disable().cors().disable();
        logger.info("Configuration complete");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
                "/swagger-ui.html", "/webjars/**");
    }


}
