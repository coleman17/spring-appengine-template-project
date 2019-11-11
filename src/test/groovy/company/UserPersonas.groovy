package company

import company.domain.User
import company.domain.UserId

import static company.support.ClockFactory.CLOCK_FACTORY

/**
 * This is a test support class that configures user personas
 * (https://medium.com/@ChamalAsela/persona-based-testing-de6e1396c23c)
 * for use with the application. When adding new ones, please describe the user's
 * attributes and what makes them different in the javadoc
 */
class UserPersonas {

    /**
     * Mike is your average user that doesnt get into trouble. Meaning,
     * he has a normal user Id and the attributes are what we would expect
     * a user to enter normally when creating them. He is a plain jane.
     * @return
     */
    static User mike() {
        return new User(new UserId("123"), "Mike PlainJane", CLOCK_FACTORY.systemClock().currentDateTime())
    }
}
