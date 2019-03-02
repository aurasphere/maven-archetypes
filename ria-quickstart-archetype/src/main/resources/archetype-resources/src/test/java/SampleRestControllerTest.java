package ${package};

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test class for {@link SampleRestController}.
 * 
 * @author drimenti
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:${artifactId}-test.xml")
@WebAppConfiguration
public class SampleRestControllerTest {

	/**
	 * Context for testing.
	 */
	@Autowired
	private WebApplicationContext webApplicationContext;

	/**
	 * Used to test Spring web components.
	 */
	private MockMvc mockMvc;

	/**
	 * Sets up the test environment.
	 */
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	/**
	 * Tests a GET REST call to {@link SampleRestController#helloWorldGet()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHelloWorldGet() throws Exception {
		// Builds the request.
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/login");

		// Sends it and checks the result.
		mockMvc.perform(request).andExpect(MockMvcResultMatchers.content().string("Hello world"));
	}


}