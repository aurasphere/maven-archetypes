package ${package};

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Esempio di web service REST con Spring.
 * 
 * @author drimenti
 * 
 */
@RestController
public class SampleRestController {

	/**
	 * Ritorna una stringa quando chiamato in GET.
	 * 
	 * @return una stringa
	 */
	@GetMapping("/test")
	public String helloWorldGet() {
		return "Hello world";
	}
	
	/**
	 * Ritorna una stringa quando chiamato in POST.
	 * 
	 * @return una stringa
	 */
	@PostMapping("/test")
	public String helloWorldPost() {
		return "Hello world";
	}

}