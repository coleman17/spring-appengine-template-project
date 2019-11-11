package company

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.Query
import com.googlecode.objectify.ObjectifyService
import company.infrastructure.datastore.Kind
import groovy.util.logging.Slf4j
import org.junit.experimental.categories.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@Category(IntegrationTest)
@ActiveProfiles("test")
@Slf4j
class DatastoreTest extends Specification {

    @Autowired
    Datastore datastore

    private Closeable ofySession

    def setup() {
        startObjectifySession()
    }

    def get(Kind kind, String keyName) {
        datastore.get(datastore.newKeyFactory().setKind(kind.kindName).newKey(keyName))
    }

    def get(Kind kind, Long keyId) {
        datastore.get(datastore.newKeyFactory().setKind(kind.kindName).newKey(keyId))
    }

    List<Entity> query(Query<Entity> query) {
        DatastoreOperationsSupport.runQuery(query, datastore)
    }

    def cleanup() {
        log.info("deleting all the kinds from datastore")
        endObjectifySession()
        for (def k : Kind.values()) {
            deleteAll(k)
        }
    }

    def deleteAll(Kind kind) {
        DatastoreOperationsSupport.deleteAll(kind, datastore)
    }

    def startObjectifySession() {
        ofySession = ObjectifyService.begin()
    }

    def endObjectifySession() {
        ofySession.close()
    }

    long entityVersion(Datastore ds, String kind, String name) {
        ds.get(ds.newKeyFactory().setKind('__entity_group__')
                .addAncestor(PathElement.of(kind, name))
                .newKey(1)).getLong('__version__')
    }
}
