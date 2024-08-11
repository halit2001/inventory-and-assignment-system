# Inventory and Assignment System
This project is an Inventory and Assignment Management System developed using Spring Boot and supported by a MySQL database. The system allows management of equipment assignments to employees, tracking of assigned equipment, and updating of assignments.

## Installation Instructions

### Prerequisites

Before you begin, ensure you have met the following requirements:

**Java Development Kit (JDK)**: Version 11 or higher is required. You can download it from the official **Oracle website** or use an open-source version such as **OpenJDK**.

**Maven**: The project uses Maven for dependency management. Download and install Maven from the **Apache Maven website**.

**MySQL**: The project uses MySQL as the database. Make sure you have MySQL installed and running. You can download it from the **MySQL official website**.

**Postman**: For testing the API endpoints. Download and install Postman from the **Postman website**.

### Cloning the Repository

**Clone the repository to your local machine using Git**:

```git clone https://github.com/halit2001/inventory-and-assignment-system```

**Navigate to the project directory**:

```cd inventory-and-assignment-system```

### Configuring the Database

**Create a new MySQL database**. You can use a MySQL client or command-line tool to create a database. For example:

```CREATE DATABASE inventory_and_assignment;```

**Configure database connection properties**. Open the src/main/resources/application.properties file and update the database connection settings:

```
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
```

### Building the Project

**Build the project using Maven**. Ensure you are in the root directory of the project and run:

```mvn clean install```

This will compile the project, run tests, and package it into a JAR file.

### Running the Application

**Run the Spring Boot application**. After building the project, you can start the application with the following command:

```mvn spring-boot:run```

Alternatively, you can run the **JAR file** directly:

 ```java -jar target/your-application-name.jar```

**Access the application by navigating to `http://localhost:8080` in your web browser.**

### Testing with Postman

**Open Postman** and create a new request.

**Authentication**

To access the API, you need to use **Basic Authentication**. Here’s how you can set it up in Postman:

**Obtain Credentials**:

You should have a username and password provided by your API or application admin.

**Configure Basic Authentication in Postman**:

Open Postman and create a new request or open an existing one.

Go to the Authorization tab.

Select Basic Auth from the Type dropdown menu.

Enter the username and password provided by your API into the Username and Password fields, respectively.

**Send Requests**:

After setting up Basic Auth, you can send requests to the API. The authentication details will be included in the headers of your requests.

**Set up the API requests:**

  **Base URL**: `http://localhost:8080`

  **HTTP Methods**: Use appropriate methods such as GET, POST, PUT, DELETE depending on the API endpoint.

  **Headers**: Set any necessary headers (e.g., Content-Type: application/json).

  **Request Body**: For POST or PUT requests, provide the JSON body as per the API requirements.

### Troubleshooting

**Common Issues**:

**Database Connection Errors**: Ensure that MySQL is running and the credentials in application.properties are correct.

**Port Conflicts**: If the application fails to start, check if port 8080 is in use and change it if necessary in application.properties.

**Postman Errors**: Ensure that the API endpoints are correctly configured in Postman and that the server is running.

**Help and Support**: For additional help, refer to the Spring Boot documentation, check the GitHub issues page for any reported problems, or visit Postman support for Postman-specific issues.
