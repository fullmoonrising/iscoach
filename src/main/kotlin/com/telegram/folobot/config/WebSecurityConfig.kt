package com.telegram.folobot.config

import com.telegram.folobot.persistence.entity.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val dataSource: DataSource
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests()
                .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name)
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .csrf()
                .ignoringRequestMatchers("/telegram-hook")
            .and()
                .formLogin().loginPage("/login").permitAll()
            .and()
                .logout().permitAll()
       return http.build()
    }

    @Bean
    fun users(dataSource: DataSource): UserDetailsManager {
        val users = JdbcUserDetailsManager(dataSource)
        users.usersByUsernameQuery = "select username, password, active from folo_web_user where username=?"
        users.setAuthoritiesByUsernameQuery("select u.username, ur.roles from folo_web_user as u inner join folo_web_user_role as ur on u.username = ur.username where u.username=?")
        return users;
    }

    @Bean
    fun getPasswordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }
}