package company.interfaces.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import company.application.UserApplicationService
import company.domain.User
import groovy.json.JsonSlurper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static company.UserPersonas.mike
import static groovy.json.JsonOutput.toJson
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class UserControllerTest extends Specification {

    static final def USER_URL = "/users/"

    MockMvc mockMvc
    UserApplicationService userApplicationService

    def setup() {
        userApplicationService = Mock()
        ObjectMapper om = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        om.registerModule(new JodaModule())
        mockMvc = standaloneSetup(new UserController(userApplicationService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(om))
                .build()
    }

    def "an api client can retrieve a user"() {
        def user = mike()
        userApplicationService.find(user.getUserId()) >> Optional.of(user)

        when:
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("${USER_URL}{id}", user.id)).andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
        def response = new JsonSlurper().parseText(result.response.contentAsString)
        response.userId.id == user.id
        response.name == user.name
        response.createdDate == user.createdDate.toString()
    }

    def "an api client can save a new user"() {
        given:
        def user = mike()

        when:
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson([
                        id         : user.getId(),
                        name       : user.getName(),
                        createdDate: user.getCreatedDate().toString()
                ]))).andReturn()

        then:
        result.response.status == HttpStatus.CREATED.value()
        1 * userApplicationService.save(_ as User)
    }
}
