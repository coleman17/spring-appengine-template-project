package company

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

import java.nio.charset.Charset

class TestAppTest extends HttpTest {

    def "it can hit the base url"() {
        when:
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/")).andReturn()

        then:
        def json = new JsonBuilder(result.response.getContentAsString(Charset.defaultCharset())).toPrettyString()
        json == '"Hello!"'
    }

    def "it has a status endpoint"() {
        when:
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")).andReturn()

        then:
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result.response.getContentAsString())
        json.status == "UP"
    }
}
