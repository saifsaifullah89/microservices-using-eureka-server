# microservices-using-eureka-server with Lombok and PostgreSQL
This is a project in which we developed two microservices and make them communicate through eureka server. 
One is Customer serivces that is communicating and consuming another serivce which is Temporary service which is 
developed to store whether the customer is a temporary customer or not.

#### Technologies:
    Spring Boot
    Eureka
    PostgreSQL database
    Project Lombok
 
As this is a maven project, in this project each microservice is executing on a different port and there are also separate ```pom.xml``` files to add dependencies related to each microservice. The main pom.xml looks like as :

```
        <dependencyManagement>
            <dependencies>
              <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.dependencies.version}</version>
                <scope>import</scope>
                <type>pom</type>
              </dependency>
              <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring.cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
              </dependency>
            </dependencies>
          </dependencyManagement>

          <dependencies>
            <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
            </dependency>
            <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
            </dependency>
          </dependencies>

          <build>
            <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
            <plugins>
              <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring.boot.maven.plugin.version}</version>
              </plugin>

            </plugins>
    
```
### Eureka Server
In this project, we implemented client-side service discovery via “Spring Cloud Netflix Eureka.”
Eureka server is used for client side service discovery and also for service registry. Here is how eureka server deshboard looks like. 
The default port to run eureka is **8761**.

![Eureka Server](https://github.com/saifsaifullah89/microservices-using-eureka-server/blob/main/eureka.png)

To add Eureka in our **Maven** project we have to add its dependency in the POM file.
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
```
**Note: For microservices it is necessary to have eureka client dependency in respective POM file.

To make the configuration for Eureka server, ```application.yml``` file looks like:
```
spring:
  application:
    name: eureka-server

server:
  port: 8761
eureka:
  client:
    fetch-registry: false
    register-with-eureka: false
 ```
The main application class has two annotations one is ```@SpringBootApplication``` and the other is used to enable the eureka server in the application that is
```@EnableEurekaServer```.

```
    @SpringBootApplication
    @EnableEurekaServer
    public class EurekaServerApplication {
        public static void main(String[] args) {
        
        SpringApplication.run(EurekaServerApplication.class,args);
      }
    }
```
 ## Customer Microservice
This microservice is to implement a customer microservice. Following are all the details of classes in this service.

#### Main Applicaiton Class:
This is main applicaiton class for Customer microservice, here we are making this as a client for eureka server so we used annotation ```@EnableEurekaClient```, all the microservices use this annotation as these are clients to eureka server, to register their service and also to discover other services in the system.
```
            @SpringBootApplication
            @EnableEurekaClient
            public class CustomerApplication {
                public static void main(String[] args) {
                    SpringApplication.run(CustomerApplication.class, args);
                }
            }

```

#### Customer Model:
This is model for Customer class, with properties of id, firstName , lastName and email. There are some tags which are from Lombok, ``` @Data, @AllArgsConstructor , @NoArgsConstructor``` these annotations are to auto generate the required getters and setters. As we are also creating a table in the database with the same name as of our model so we annotated it as ```@Entity```.
```
        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        public class Customer {

            @Id
            @SequenceGenerator(
                    name = "customer_id_sequence",
                    sequenceName = "customer_id_sequence"
            )
            @GeneratedValue(
                    strategy = GenerationType.SEQUENCE,
                    generator = "customer_id_sequence"
            )
            private Integer id;
            private String firstName;
            private String lastName;
            private String email;
        }
```
#### Customer Controller:
Here is the Controller class for this microservice,it is a RESTController class to expose APIs for creating and fetching resources.
Here we are just creating a resource so just using ```@PostMapping``` annotation. We are also using logging on the console so we used ```@Slf4j``` as we know that it is considered as default logging framework.

```
        @Slf4j
        @AllArgsConstructor
        @RestController
        @RequestMapping("api/v1/customers")
        public class CustomerController{

            private final CustomerService customerService;

            @PostMapping()
            public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
                log.info("new customer registration {}", customerRegistrationRequest);
                customerService.registerCustomer(customerRegistrationRequest);
            }
        }
```
#### Customer Service Class
This is a service class responsible for creation and manipulation of resources in the repository (Database), here in this class ```customer microservice ``` is also consuming a resource from another microservices named as ```temporary microservice ```

```
        @AllArgsConstructor
        @Service
        public class CustomerService{

            private final CustomerRepository customerRepository;
            private final RestTemplate restTemplate;

            public void registerCustomer(CustomerRegistrationRequest request) {
                Customer customer= Customer.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(request.email())
                        .build();

                //store customer in db
                customerRepository.saveAndFlush(customer);
                
                
                //check if customer is temporary
               TemporaryCheckResponse temporaryCheckResponse= restTemplate.getForObject(

                               "http://TEMPORARY/api/v1/temporary/{customerId}",
                               TemporaryCheckResponse.class,
                               customer.getId()
                       );

                assert temporaryCheckResponse != null;
                if(temporaryCheckResponse.isTemporary()){
                   throw new IllegalStateException("is Temporary Customer.");
               }


            }
        }
```

#### Application.yml file for configuration
In this microservice the ```application.yml file looks like as following. In this file we configured the data base and also we configured this microservice as eureka client. 
```
server:
  port: 8080

spring:
  application:
    name: customer
  jpa:
    database: POSTGRESQL
    show-sql: true
    hibernate:
      ddl-auto: create-drop
      formate_sql: true
  datasource:
    platform: postgres
    url: jdbc:postgresql://localhost:5432/customer
    username: postgres
    password: 4321
    driverClassName: org.postgresql.Driver

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
    fetch-registry: true
    register-with-eureka: true
```
### Temporary Microservice
This microservice is used to hold the data about the status of the customer whether it is a temporary customer or not. This is a simple microservice with its own configuration and also with its own implementation. It also runs on a separate port. Following are the some portions from the implementation of temporary microservice.

#### Temprary check model 

```
        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Entity
        public class TemporaryCheckHistory{

            @Id
            @SequenceGenerator(
                    name = "temporary_id_sequence",
                    sequenceName = "temporary_id_sequence"
            )
            @GeneratedValue(
                    strategy = GenerationType.SEQUENCE,
                    generator = "temporary_id_sequence"
            )
            private Integer id;
            private Integer customerId;
            private Boolean isTemporary;
            private LocalDateTime createdAt;

        }
```

#### Temporary Controller

```
            @Slf4j
            @AllArgsConstructor
            @RestController
            @RequestMapping("api/v1/temporary/")
            public class TemporaryController {

                private final TemporaryService temporaryService;



                @GetMapping(path = "{customerId}")
                public TemporaryCheckResponse isTemporary(@PathVariable("customerId") Integer customerId){

                   boolean isTemporaryCustomer= temporaryService.isTemporaryCustomer(customerId);
                   log.info("Temporary Service Check for customer {}", customerId);
                    return new TemporaryCheckResponse(isTemporaryCustomer);
                }
            }

```

#### Pom.xml and application.yml files:

**pom.xml** with all the required dependencies:
```
           <dependencies>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-web</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-data-jpa</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.postgresql</groupId>
                    <artifactId>postgresql</artifactId>
                    <scope>runtime</scope>
                </dependency>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
                </dependency>
            </dependencies>
 ```

**Application.yml file:
```
        server:
          port: 8082
        spring:
          application:
            name: temporary
          jpa:
            database: POSTGRESQL
            show-sql: true
            hibernate:
              ddl-auto: create-drop
          datasource:
            platform: postgres
            url: jdbc:postgresql://localhost:5432/temporary
            username: postgres
            password: 4321
            driverClassName: org.postgresql.Driver

        eureka:
          client:
            service-url:
              defaultZone: http://localhost:8761/eureka
            fetch-registry: true
            register-with-eureka: true
    
```

### This readme file just explaining some of the important parts of the project.
