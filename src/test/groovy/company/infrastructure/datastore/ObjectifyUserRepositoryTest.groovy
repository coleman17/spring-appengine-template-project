package company.infrastructure.datastore

import company.DatastoreTest
import org.springframework.beans.factory.annotation.Autowired

import static com.googlecode.objectify.ObjectifyService.ofy
import static company.UserPersonas.mike

class ObjectifyUserRepositoryTest extends DatastoreTest {

    @Autowired
    ObjectifyUserRepository subject

    def "it can save and find a user"() {
        given:
        def user = mike()

        when:
        subject.save(user)
        ofy().clear()

        then:
        def found = subject.find(user.getUserId()).get()
        found.getId() == user.getId()
        found.getName() == user.getName()
        found.getCreatedDate().toString() == user.getCreatedDate().toString()
    }
}
