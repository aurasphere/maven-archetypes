package ${package};

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring REST WS sample.
 * 
 * @author Donato Rimenti
 * 
 */
@RestController
public class SampleRestController {

	/**
	 * Returns a String when called in GET.
	 * 
	 * @return a string
	 */
	@GetMapping("/test")
	public String helloWorldGet() {
		return "Hello world";
	}
	
	/**
	 * Returns a String when called in POST.
	 * 
	 * @return a string
	 */
	@PostMapping("/test")
	public String helloWorldPost() {
		return "Hello world";
	}

}