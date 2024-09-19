# Spring Web MVC Documentation

## Overview

**Spring Web MVC** is a module within the Spring Framework that provides support for developing web applications using the Model-View-Controller (MVC) design pattern. It offers a robust, scalable, and flexible approach to building web applications with Java.

### Key Concepts

- **Model**: Represents the data or business logic of the application. It is typically represented by Java objects.
- **View**: Responsible for rendering the model data to the user. It is usually implemented using JSP, Thymeleaf, or other view technologies.
- **Controller**: Handles user requests, processes them, and returns a view or data. It acts as an intermediary between the model and the view.

---

## Core Components

### 1. DispatcherServlet

The `DispatcherServlet` is the core component of Spring Web MVC. It acts as the front controller that handles all incoming HTTP requests and dispatches them to the appropriate handlers.

- **Configuration**: Defined in `web.xml` or using Java configuration.

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    // Configuration beans
}
```
### Controllers

Controllers are responsible for handling user requests and returning responses. They are annotated with @Controller or @RestController.

`@Controller`: Used in traditional web applications to return views.

```java
@Controller
public class MyController {

    @RequestMapping("/greeting")
    public String greeting(Model model) {
        model.addAttribute("message", "Hello, World!");
        return "greeting"; // View name
    }
}
```

`@RestController`: Used in RESTful services to return data (e.g., JSON or XML).

```java
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/items")
    public List<Item> getItems() {
        return Arrays.asList(new Item("item1"), new Item("item2"));
    }
}
```

### 3. View Resolvers

View resolvers are responsible for resolving view names to actual view templates. Common view resolvers include:

`InternalResourceViewResolver`: Resolves views based on JSP files.

```java
@Bean
public InternalResourceViewResolver viewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    return resolver;
}
```

`ThymeleafViewResolver`: Resolves views using Thymeleaf templates.

```java
@Bean
public ThymeleafViewResolver thymeleafViewResolver() {
    ThymeleafViewResolver resolver = new ThymeleafViewResolver();
    resolver.setTemplateEngine(templateEngine());
    return resolver;
}

@Bean
public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(templateResolver());
    return engine;
}


@Bean
public ITemplateResolver templateResolver() {
    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
    resolver.setPrefix("classpath:/templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode(TemplateMode.HTML);
    return resolver;
}
```
4. Model and View
The ModelAndView class combines model data and view information in a single object.

```java
@RequestMapping("/model")
public ModelAndView modelExample() {
    ModelAndView modelAndView = new ModelAndView("modelView");
    modelAndView.addObject("message", "Model and View Example");
    return modelAndView;
}
```

5. Request Mapping

Mapping HTTP requests to handler methods is done using the @RequestMapping annotation, along with its specialized variants such as @GetMapping, @PostMapping, @PutMapping, and @DeleteMapping.

```java
@RequestMapping(value = "/welcome", method = RequestMethod.GET)
public String welcome() {
    return "welcome";
}

@GetMapping("/welcome")
public String welcome() {
    return "welcome";
}
```
6. Exception Handling
Exception handling can be managed globally using @ControllerAdvice or locally within controllers using @ExceptionHandler.

Global Exception Handling:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", e.getMessage());
        return modelAndView;
    }
}
```
Local Exception Handling:

```java
@Controller
public class MyController {

    @RequestMapping("/error")
    public String error() throws Exception {
        throw new Exception("An error occurred");
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }
}
```

### Configuration
Java Configuration
Spring Web MVC can be configured using Java-based configuration with @Configuration and @EnableWebMvc.

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    // Configuration methods
}
```

XML Configuration
Alternatively, you can use XML configuration to set up Spring Web MVC.

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="org.springframework.web.servlet.DispatcherServlet">
        <property name="contextConfigLocation" value="/WEB-INF/applicationContext.xml"/>
    </bean>

    <bean id="viewResolver"
          class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```

### Best Practices
- **Separation of Concerns**: Keep controllers focused on handling requests and delegating business logic to services.
- **Error Handling**: Use centralized error handling to manage exceptions and provide user-friendly error messages.
- **Validation**: Implement input validation using JSR-303/JSR-380 annotations and handle validation errors gracefully.
- **Security**: Integrate Spring Security to protect your web application from unauthorized access and other security threats.

### Resources
- **Spring Web MVC Reference Documentation**
- **Spring Guides**
- **Spring Boot Documentation**

This documentation provides an overview of Spring Web MVC, including its core components, configuration, and best practices. For more detailed information, refer to the provided resources.