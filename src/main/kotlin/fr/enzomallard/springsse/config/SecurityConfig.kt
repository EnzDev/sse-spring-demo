package fr.enzomallard.springsse.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
class SecurityConfig {

    @Bean
    fun userDetailsService(): UserDetailsService {
        val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

        listOf(
            "user" to listOf("USER"), // Authorize: Basic dXNlcjpwYXNzd29yZA==
            "admin" to listOf("USER", "DATA_ADMIN", "ADMIN"), // Authorize: Basic YWRtaW46cGFzc3dvcmQ=
            "manager" to listOf("USER", "DATA_ADMIN") // Authorize: Basic bWFuYWdlcjpwYXNzd29yZA==
        ).map {
            User.builder()
                .username(it.first)
                .password(passwordEncoder.encode("password"))
                .roles(*it.second.toTypedArray())
                .build()
        }.let { (user, admin, userEv) ->
            return InMemoryUserDetailsManager(user, userEv, admin)
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/test").permitAll()
                it.requestMatchers("/api/data/**").authenticated()
            }.httpBasic(Customizer.withDefaults())
            .build()
}