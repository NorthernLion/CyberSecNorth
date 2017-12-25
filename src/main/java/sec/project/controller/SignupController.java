package sec.project.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;
import javax.servlet.http.HttpSession;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;
    
    @Autowired
    private HttpSession session;
    
    
    @PostConstruct
    public void init() {
        Signup signup = new Signup();
        Signup signup1 = new Signup();
        signup.setName("Admin");
        signup.setPassword("qwerty1234");
        signup.setAddress("Behind you");
        signup1.setName("Ted");
        signup1.setPassword("godKing");
        signup1.setAddress("Kumpula");
    }
    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name,@RequestParam String address,@RequestParam String password) {
        Signup signup = new Signup();
        signup.setName(name);
        signup.setAddress(address);
        signup.setPassword(password);
        signupRepository.save(signup);
        session.setAttribute("name", name);
        session.setAttribute("password", address);
        return "redirect:/done";
    }
    
    @GetMapping("/done")
    public String donetylo() {
        return "done";
    }
    


    @GetMapping("/find")
    public String all(Model model) {
        List<Signup> signups = signupRepository.findAll();
        model.addAttribute("signups", signups);
        return "all";
    }
    
    
    @GetMapping("/logout")
    public String logout(Model model) {
        session.removeAttribute("name");
        session.removeAttribute("password");
        return "redirect:/form";
    }    

}
