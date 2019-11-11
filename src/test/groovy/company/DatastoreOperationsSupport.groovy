package company

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Query
import company.infrastructure.datastore.Kind

class DatastoreOperationsSupport {
    static <T> List<T> runQuery(Query<T> query, Datastore datastore) {
        def list = new ArrayList<T>()
        datastore.run(query).each { entity -> list.add(entity) }
        list
    }

    static void deleteAll(Kind kind, Datastore datastore) {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(kind.getKindName())
                .build()
        datastore.run(query).each { entity ->
            datastore.delete(entity.key)
        }
    }
}
