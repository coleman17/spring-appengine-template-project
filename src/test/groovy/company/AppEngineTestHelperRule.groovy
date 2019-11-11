package company

import com.google.appengine.tools.development.testing.LocalServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class AppEngineTestHelperRule implements TestRule {

    private LocalServiceTestHelper helper

    AppEngineTestHelperRule(LocalServiceTestConfig... configs) {
        this.helper = new LocalServiceTestHelper(configs)
    }

    @Override
    Statement apply(Statement base, Description description) {
        return {
            try {
                helper.setUp()
                base.evaluate()
            } finally {
                helper.tearDown()
            }
        }
    }
}
