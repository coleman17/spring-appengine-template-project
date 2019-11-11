package company.infrastructure.datastore.objectify;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.cache.IdentifiableValue;
import com.googlecode.objectify.cache.MemcacheService;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of Objectify's MemcacheService that uses App Engine Standard's memcache service.
 */
@Slf4j
public class GaeMemcacheService implements MemcacheService {
    private final com.google.appengine.api.memcache.MemcacheService service;

    public GaeMemcacheService() {
        this(MemcacheServiceFactory.getMemcacheService());
    }

    public GaeMemcacheService(@NonNull com.google.appengine.api.memcache.MemcacheService service) {
        this.service = service;
    }

    @Override
    public Object get(String key) {
        log.debug("get {}", key);
        return service.get(key);
    }

    @Override
    public Map<String, IdentifiableValue> getIdentifiables(Collection<String> keys) {
        Map<String, IdentifiableValue> ret = new HashMap<>();
        Map<String, com.google.appengine.api.memcache.MemcacheService.IdentifiableValue> map = service.getIdentifiables(keys);
        for (String key : keys) {
            if (map.containsKey(key)) {
                ret.put(key, new OldAppengineMemcacheIdentifiableValue(map.get(key)));
            } else {
                //create a slot in the cache for this key - that's what objectify expects to happen in here
                put(key, null);
                ret.put(key, new OldAppengineMemcacheIdentifiableValue(service.getIdentifiable(key)));
            }
        }
        log.debug("getIdentifiables for {} returning {}", keys, (ret.isEmpty() ? "empty" : ret));
        return ret;
    }

    @Override
    public Map<String, Object> getAll(Collection<String> keys) {
        Map<String, Object> ret = service.getAll(keys);
        log.debug("getAll {} returned {}", keys, ret);
        return ret;
    }

    @Override
    public void put(String key, Object thing) {
        log.debug("put {} at {}", thing, key);
        service.put(key, thing);
    }

    @Override
    public void putAll(Map<String, Object> values) {
        log.debug("putAll {}", values);
        service.putAll(values);
    }

    @Override
    public Set<String> putIfUntouched(Map<String, CasPut> values) {
        log.debug("putIfUntouched {}", values);
        Map<String, com.google.appengine.api.memcache.MemcacheService.CasValues> casValues = new HashMap<>();
        values.forEach((key, value) -> {
            com.google.appengine.api.memcache.MemcacheService.CasValues vo =
                    new com.google.appengine.api.memcache.MemcacheService.CasValues(
                            ((OldAppengineMemcacheIdentifiableValue) value.getIv()).value, value.getNextToStore(),
                            //objectify treats 0 as forever, but appengine memcache treats 0 as immediate
                            // expiration. so we translate to what what app engine memcache wants
                            value.getExpirationSeconds() == 0 ? null : Expiration.byDeltaSeconds(value.getExpirationSeconds()));
            casValues.put(key, vo);
        });
        return service.putIfUntouched(casValues);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        service.deleteAll(keys);
    }

    @Data
    static class OldAppengineMemcacheIdentifiableValue implements IdentifiableValue {
        private final com.google.appengine.api.memcache.MemcacheService.IdentifiableValue value;

        @Override
        public Object getValue() {
            return value.getValue();
        }

    }
}
