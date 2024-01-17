# ssm-devservices-test

Run `./mvnw test` and test will fail with:

```
software.amazon.awssdk.services.ssm.model.ParameterNotFoundException: Parameter hello not found.
....
```

Go to `GreetingResourceTest` and enable workaround test resource:
```java
@QuarkusTestResource(SsmReadinessProbeTestResource.class)
```

Run `./mvnw test` again and it will succeed.