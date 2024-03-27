package pl.futurecollars.invoicing

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class AppTest extends Specification {

    def "must test to cover main"() {
        setup:
        def app = new App()

        and:
        app.main()
    }
}
