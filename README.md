# OpenAPI generated API stub

Spring Framework stub


## Overview
This code was generated by the [OpenAPI Generator](https://openapi-generator.tech) project.
By using the [OpenAPI-Spec](https://openapis.org), you can easily generate an API stub.
This is an example of building API stub interfaces in Java using the Spring framework.

The stubs generated can be used in your existing Spring-MVC or Spring-Boot application to create controller endpoints
by implementing `@Service` classes that implement the delegate interface. Eg:

```java
@Service
public class GreetingService implements GreetingApiDelegate {
// implement all GreetingApiDelegate methods
}
```

## Install Azurite (Docker)

```bash
docker pull mcr.microsoft.com/azure-storage/azurite
```

## Run Azurite

```bash
docker run -p 10000:10000 -p 10001:10001 -p 10002:10002 \
    mcr.microsoft.com/azure-storage/azurite
```

## Interesting Demo Files

* `application.yaml` -- Swagger Endpoint Configuration
* `GreetingService` -- OpenAPI Delegate Pattern implementation
* `ControllerExceptionHandler` -- `RestControllerAdvice` for global controller exception handling
* `PersonRepository` -- Azure Table Storage demo
