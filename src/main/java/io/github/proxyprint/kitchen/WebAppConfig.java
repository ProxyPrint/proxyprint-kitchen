/* 
 * Copyright 2016 Jorge Caldas, José Cortez
 * José Francisco, Marcelo Gonçalves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.proxyprint.kitchen.utils.gson.AnnotationExclusionStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableCaching
public class WebAppConfig {

    public static void main(String[] args) {
        SpringApplication.run(WebAppConfig.class, args);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            Integer port;
            try {
                port = Integer.valueOf(System.getenv("PORT"));
            } catch (NumberFormatException ex) {
                port = 8080;
            }
            container.setPort(port);
        });
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Add REST allowed endpoints...
                registry.addMapping("/**").allowedOrigins("http://localhost:9000");
            }
        };
    }
    
    @Bean
    public Gson gson(){
        return new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
    }
}
