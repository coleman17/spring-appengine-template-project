package company

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import company.infrastructure.datastore.jackson.AbstractJacksonConfig
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class TestDataFactory {

    static def customizedJacksonConverter() {
        ObjectMapper om = new ObjectMapper()
        om.registerModule(new JodaModule())
        InjectableValues.Std values = new InjectableValues.Std()
        values.addValue(AbstractJacksonConfig.MISSING_FIELD_VALUE_KEY, "?")
        om.setInjectableValues(values)
        return new MappingJackson2HttpMessageConverter(om)
    }
}
