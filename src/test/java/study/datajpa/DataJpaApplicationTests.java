package study.datajpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") 스프링 부트는 필요X
class DataJpaApplicationTests {

	@Test
	void contextLoads() {
	}

}
