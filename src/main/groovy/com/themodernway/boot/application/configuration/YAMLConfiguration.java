
package com.themodernway.boot.application.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

@Configuration
public class YAMLConfiguration implements WebMvcConfigurer
{
    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters)
    {
        converters.add(new YAMLJackson2HttpMessageConverter());
    }

    private static final class YAMLJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter
    {
        public YAMLJackson2HttpMessageConverter()
        {
            super(new YAMLJacksonMapper(), MediaType.valueOf("application/x-yaml"), MediaType.valueOf("application/yaml"), MediaType.valueOf("text/yaml"), MediaType.valueOf("text/yml"));
        }
    }

    private static final class YAMLJacksonMapper extends YAMLMapper
    {
        private static final long serialVersionUID = 1L;

        public YAMLJacksonMapper()
        {
            enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);

            enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        }
    }
}
