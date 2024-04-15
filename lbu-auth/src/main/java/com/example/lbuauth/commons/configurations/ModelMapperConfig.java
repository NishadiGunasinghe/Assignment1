package com.example.lbuauth.commons.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    // Configures and provides a ModelMapper bean for mapping objects
    @Bean
    public ModelMapper modelMapper() {
        // Instantiate a new ModelMapper
        ModelMapper modelMapper = new ModelMapper();
        // Ignore ambiguous mappings to prevent potential conflicts
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        // Return the configured ModelMapper instance
        return modelMapper;
    }
}
