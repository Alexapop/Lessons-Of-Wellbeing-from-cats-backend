package dev.ioana.lessons_of_wellbeing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=",
    "demo.user.password="
})
class LessonsOfWellbeingApplicationTests {

	@Test
	void contextLoads() {
	}

}
