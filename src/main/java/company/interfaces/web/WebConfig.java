package company.interfaces.web;

import com.googlecode.objectify.ObjectifyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

/**
 * Configuration for the web interfaces
 */
@Configuration
@EnableHypermediaSupport(type = HAL)
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<ObjectifyFilter> objectifyFilter() {
        var registrationBean = new FilterRegistrationBean<ObjectifyFilter>();
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setFilter(new ObjectifyFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
