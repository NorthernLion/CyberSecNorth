## 1. Cross-Site Scripting (XSS)

Reproduce:
1. Go to http://localhost:8080/form
2. Enter username: "Me", Password: "You", address: "<script>alert("lol");</script>" and press submit
3. Go to http://localhost:8080/find
4. Popup saying "lol" will appear

This error is happening because in the document all.html (src/main/recources/templates) utext is used instead of text for the address field.
This causes it to be possible to enter dangerous javascript into the adress of the site.

Fixing this is easy. Simply editthe document all.html from

        <ol>
            <li th:each="signup : ${signups}">
                <span th:text="${signup.name}">name</span>
                <span th:utext="${signup.address}">address</span>
            </li>
        </ol>
        
to        
        
        <ol>
            <li th:each="signup : ${signups}">
                <span th:text="${signup.name}">name</span>
                <span th:text="${signup.address}">address</span>
            </li>
        </ol>

## 2. Missing Function Level Access Control

Reproduce:
1. Go to http://localhost:8080/form
2. Enter username: "Me", Password: "You", address: "He"
3. Click LogOut on the page you arrived to
4. Go to http://localhost:8080/find
5. You will be able to see the page all.html even though you should not be able to!

Fixing this can be done by modifying method all in the SignupController in the sec.project.controller package from

    @GetMapping("/find")
    public String all(Model model) {
        List<Signup> signups = signupRepository.findAll();
        model.addAttribute("signups", signups);
        return "all";
    }
    
    
to 
    
        @GetMapping("/find")
    public String all(Model model) {
        if (session.getAttribute("name") == null || session.getAttribute("password") == null) {
            return "redirect:/form"; 
        }
        List<Signup> signups = signupRepository.findAll();
        model.addAttribute("signups", signups);
        return "all";
    }
    
as these attributes are set when sumbimit form (method submitForm) and are cleared when loggingout (method logout) they can be 
used to keep track of if you have submited your info to event or not.

## 3. Using Components with Known Vulnerabilities (& Security Misconfiguration)

In the Pom.Xml file

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.2.RELEASE</version>
    </parent>
    
This version is outdated. New versions of the Spring framework boot starter parent can be viewed at the following website:
https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent

We can fix this by using newer version, for example 1.5.9.RELEASE by changing pom.xml to

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
    </parent>




    
## 4. Cross-Site Request Forgery (CSRF)

Reproduce:
1. Go to http://localhost:8080/form
2. Enter username: "Me", Password: "You", address: "He"
3. Simulate an evil CSRF attack by going to website http://localhost:8080/logout
4. You have been logged out and can no longer see the page http://localhost:8080/find (Assuming you have done the modifications to the code given in part 2)

Fixing:

1. Signupcontroller


    @GetMapping("/logout")
    
should be
    
    @PostMapping("/logout")
    
2. done html file
    
       <form action="#" th:action="@{/logout}" method="GET">
    
should be 

    <form action="#" th:action="@{/logout}" method="POST">

3. SecurityConfiguration

Remove line

     http.csrf().disable();
  
  
Making logout and all sort of datamodification methods POST type makes it harder for other websites to do CSRF attacks on you.
But just removing the line http.csrf().disable(); should be enough to make csrf attacks fail on your application.


## 5. Security Misconfiguration (& Cross-Site Request Forgery (CSRF))

Simply look at the class SecurityConfiguration.java in package sec.project.config :

        http.csrf().disable();
        http.headers().disable();
        
These two lines of code disable protection agaisnt csrf attacks and disable http.headers. Beacause of this CRSF attacks work agaisnt the application
and http headers do not save protective data that they should.
Fixing the problem is easy. Just deleting both of these lines will fix the issue. In many of our assignments in this course csrf was disabled in order
for the tests to work properly. It is good to understand that this should not be disabled!!!
    
