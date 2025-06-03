# How to Run
Ensure Docker is installed and running on your machine.

Navigate to the directory containing the docker-compose.yaml file.
Start the services using the following command:
```bash
docker-compose up -d
```
Verify the services are running:

Redis: Check the logs or run:
```bash
docker logs redis-wallets
```
You can also test connectivity:
```bash
redis-cli ping
```

CockroachDB: Check the logs or run:
```bash
docker logs cockroachdb-wallets
```

# How to Run the Application via Command Line
Ensure Java 17 or higher and Maven are installed.

Clone the repository:
```bash
git https://github.com/AndreCarvalho94/ms-wallet
cd ms-wallet
```
Build the application:
```bash
mvn clean install
```
Run the application:

```bash
mvn spring-boot:run
```

How to Test the Application Using Maven
Ensure your docker service is running.
Run the tests:

```bash
mvn test
```

Check the test results in the console output. Ensure all tests pass.

# Design Choices
### CockroachDB 

was chosen for its distributed architecture, ensuring high availability and scalability. It provides strong transactional consistency and automatic replication, making it ideal for financial applications.


### Redis for Idempotency
Redis is used to manage idempotency keys due to its low latency and atomic operations. It allows quick storage and retrieval of unique identifiers to prevent duplicate requests.


### Retry with Jitter

The retry mechanism with jitter was implemented to avoid service overload during retries. Jitter introduces randomization in retry intervals, reducing the risk of simultaneous retry spikes.


### Controlled Concurrency with @Version
The @Version annotation from JPA is used for optimistic concurrency control. It ensures that simultaneous updates to the same record are detected and handled properly.


### Consolidated Balance in Balance Table
A dedicated Balance table was created to store the consolidated wallet balance. This design enables fast queries and avoids repetitive runtime calculations.


### Balance Also Stored in Transaction Table
The balance is also stored in the Transaction table to maintain a detailed history of changes. This approach supports auditing and tracking of financial operations.

### Event-Driven Architecture
ApplicationEventPublisher was chosen to implement an event-driven architecture, simplifying integration with message brokers like Kafka or RabbitMQ. Events are published and consumed in a decoupled manner, promoting scalability and flexibility in service communication and also creating redundancy to improve traceability and reliability of operations.

### Rest Assured and Testcontainers
Rest Assured is used for API integration testing, ensuring precise validation of responses. Testcontainers provides isolated test environments with Redis and CockroachDB, simulating real application behavior.

### Trade-offs and Compromises
Due to time constraints, several aspects of the project were simplified or deferred for future improvements:


Security Implementation: A more robust implementation, such as OAuth2 or JWT-based authentication, was postponed. This would ensure better protection of sensitive data and user authentication.


API Documentation: Swagger was integrated for API documentation, but a more detailed and comprehensive setup, including examples and error responses, could enhance usability for developers.


Layer Separation: The current architecture follows a service-repository pattern, but adopting Clean Architecture with distinct layers like gateways would improve modularity and testability.


Error Handling: Error handling is functional but could be extended to include standardized error codes and detailed logging for better debugging and user feedback.


Scalability Enhancements: While the system is designed to scale, additional optimizations, such as caching strategies and asynchronous processing, could further improve performance under heavy load.


These trade-offs were made to prioritize delivering a functional product within the given timeline, with plans to address these areas in future iterations.
