\# MiniShop



MiniShop is a full-stack e-commerce web application developed using Java and Spring Boot. It provides product browsing, user authentication, product management, purchasing functionality, stock management, and order tracking.



The application supports two user roles: regular users and administrators. Regular users can browse products, purchase available items, and view their order history. Administrators can manage products and users, reset the product catalog, and monitor all customer orders.



\---



\## Screenshots



\### Home Page



!\[MiniShop Home Page](screenshots/home.png)



\### Login Page



!\[MiniShop Login Page](screenshots/login.png)



\### Products Page



!\[MiniShop Products Page](screenshots/products.png)



\---



\## Features



\### Product Catalog



\- Browse available products.

\- View individual product details.

\- Search products by name.

\- Filter products by price.

\- Sort products.

\- Display product prices and available stock.

\- Hide out-of-stock products from the public catalog.



\### User Authentication and Management



\- Register a new user account.

\- Log in securely.

\- Password hashing using Spring Security.

\- Role-based access control.

\- Separate permissions for administrators and regular users.

\- Administrators can view and manage registered users.

\- Prevent administrators from deleting their own account.

\- Prevent deletion of the last administrator.



\### Product Management



\- Create new products.

\- Edit existing products.

\- Delete products.

\- Reset the product catalog to predefined default products.

\- Automatically initialize the default catalog when the database is empty.



\### Purchasing and Order Management



\- Purchase products in selected quantities.

\- Validate available stock before purchasing.

\- Automatically decrease stock after a successful purchase.

\- Prevent purchases exceeding available stock.

\- Prevent negative stock quantities.

\- View personal order history.

\- Administrators can view all customer orders.

\- Preserve historical product names and purchase prices in order records.



\### Database and Data Management



\- PostgreSQL database integration.

\- Persistent database storage using Docker volumes.

\- Users and orders are connected through database relationships.

\- Deleting a user also deletes their associated orders and order items.

\- Resetting the product catalog preserves historical order information.



\---



\## Technology Stack



| Technology | Purpose |

|---|---|

| Java 21 | Programming language |

| Spring Boot 4.1.2 | Application framework |

| Spring MVC | Web application |

| Spring Data JPA | Database access |

| Hibernate | Object-relational mapping |

| Spring Security | Authentication and authorization |

| Thymeleaf | Server-side HTML rendering |

| PostgreSQL 17 | Relational database |

| HTML5 | Page structure |

| CSS3 | Styling |

| Maven | Build and dependency management |

| JUnit 5 | Automated testing |

| Mockito | Mocking and unit testing |

| Docker | Database containerization |

| Docker Compose | Container management |

| Git and GitHub | Version control |



\---



\## Requirements



Before running the application, install the following software:



\- \*\*Java Development Kit 21\*\*

\- \*\*Apache Maven\*\*

\- \*\*Docker Desktop\*\*

\- \*\*Git\*\*



You can verify your installations using PowerShell:



```powershell

java -version

mvn -version

docker --version

docker compose version

git --version

```



Make sure Java reports version 21 and Docker Desktop is running.



\---



\# Installation and Setup



Follow these instructions to run MiniShop on a Windows computer.



\## Step 1: Clone the Repository



Open PowerShell or the terminal in IntelliJ IDEA.



Navigate to the directory where you want to store the project.



Clone the repository:



```powershell

git clone https://github.com/Tomey-SNR/java-minishop.git

```



Navigate into the project directory:



```powershell

cd java-minishop

```



You should now be in the project root directory, where `pom.xml` is located.



\## Step 2: Configure Environment Variables



MiniShop uses a `.env` file to store database settings and administrator credentials.



The actual `.env` file is excluded from Git because it contains local configuration and sensitive information.



The repository includes a `.env.example` file as a template.



Create your local `.env` file by running:



```powershell

Copy-Item .env.example .env

```



Open the newly created `.env` file in IntelliJ IDEA or another text editor.



Configure the following variables:



```dotenv

DB\_URL=jdbc:postgresql://localhost:5433/minishop

DB\_USERNAME=minishop

DB\_PASSWORD=your\_database\_password



ADMIN\_USERNAME=admin

ADMIN\_EMAIL=admin@minishop.local

ADMIN\_PASSWORD=your\_secure\_admin\_password

```



Replace the example passwords with your own values.



\### Environment Variables Explained



| Variable | Description |

|---|---|

| `DB\_URL` | PostgreSQL database connection URL |

| `DB\_USERNAME` | Database username |

| `DB\_PASSWORD` | Database password |

| `ADMIN\_USERNAME` | Initial administrator username |

| `ADMIN\_EMAIL` | Initial administrator email |

| `ADMIN\_PASSWORD` | Initial administrator password |



All six variables are required.



MiniShop validates the `.env` file during application startup. If the file is missing or a required variable is empty, the application stops with an explanatory error message.



\*\*Important:\*\* Never commit your real `.env` file to GitHub. Only commit `.env.example` with placeholder values.



\## Step 3: Start the PostgreSQL Database



MiniShop uses PostgreSQL as its database.



Docker Compose provides the database environment without requiring a separate local PostgreSQL installation.



Make sure Docker Desktop is running.



From the project root, execute:



```powershell

docker compose up -d

```



This starts the PostgreSQL container in the background.



Check whether the container is running:



```powershell

docker compose ps

```



You should see the PostgreSQL service with a running status.



The database is exposed on:



```text

localhost:5433

```



The application connects to the database using the `DB\_URL`, `DB\_USERNAME`, and `DB\_PASSWORD` settings in `.env`.



\### Database Persistence



Docker Compose uses a named volume to preserve database data.



Stopping or restarting the container does not normally delete stored data.



\## Step 4: Build the Application



Before starting MiniShop, compile and test the project using Maven:



```powershell

mvn clean install

```



This command:



1\. Removes previous build output.

2\. Compiles the source code.

3\. Runs the automated tests.

4\. Packages the application.

5\. Verifies that the build completes successfully.



Wait until Maven reports:



```text

BUILD SUCCESS

```



\## Step 5: Start MiniShop



From the project root, execute:



```powershell

mvn spring-boot:run

```



Spring Boot will:



1\. Load the application configuration.

2\. Validate the `.env` file.

3\. Connect to PostgreSQL.

4\. Initialize the administrator account.

5\. Initialize the default product catalog if the database is empty.

6\. Start the web server.



When startup is successful, open the application in your browser:



\*\*http://localhost:8080\*\*



\## Step 6: Log In as Administrator



Use the administrator credentials configured in your `.env` file:



```text

Username: value of ADMIN\_USERNAME

Password: value of ADMIN\_PASSWORD

```



The administrator can access:



\- Product management.

\- User management.

\- Order management.

\- Product catalog reset functionality.



The application also supports regular user registration through the registration page.



Register a regular account to explore the customer functionality.



\---



\# Running Automated Tests



MiniShop includes automated tests covering application functionality and business rules.



To execute the complete test suite, run:



```powershell

mvn clean test

```



The verified project version has \*\*111 passing automated tests\*\*.



The tests cover areas including:



\- User registration and validation.

\- Authentication and authorization.

\- Product management.

\- Product catalog initialization and reset.

\- Purchasing and stock validation.

\- Order creation and order history.

\- Administrator functionality.

\- User deletion and associated data cleanup.

\- Controller and service behavior.

\- Database integration.



A successful test execution should finish with:



```text

BUILD SUCCESS

```



\---



\# Stopping the Application



To stop the Spring Boot application, press:



```text

Ctrl + C

```



To stop the PostgreSQL container while preserving its database volume, execute:



```powershell

docker compose down

```



To start the database again later:



```powershell

docker compose up -d

```



\*\*Important:\*\* Avoid using `docker compose down -v` unless you intentionally want to delete the persistent database volume and its data.



\---



\# Troubleshooting



\## Application Cannot Find the `.env` File



Make sure you created `.env` from `.env.example`:



```powershell

Copy-Item .env.example .env

```



Make sure the file is located in the project root, alongside `pom.xml`.



\## Missing Environment Variable Error



Open `.env` and check that all six required variables are present and have non-empty values.



\## Database Connection Error



Check that:



1\. Docker Desktop is running.

2\. The PostgreSQL container is running.

3\. The database port is correctly configured.

4\. The database username and password match your configuration.



Check the containers:



```powershell

docker compose ps

```



View container logs:



```powershell

docker compose logs

```



\## Port 8080 Is Already in Use



Another application may already be using port 8080.



Stop the other application or configure MiniShop to use a different server port.



\## Maven Build Fails



Check your Java version:



```powershell

java -version

```



Make sure JDK 21 is installed and configured correctly.



You can also try:



```powershell

mvn clean test

```



Read the first reported compilation or test error to identify the cause.



\---



\# Project Structure



The project follows a layered Spring Boot architecture.



```text

java-minishop/

│

├── screenshots/

│   ├── home.png

│   ├── login.png

│   └── products.png

│

├── src/

│   ├── main/

│   │   ├── java/

│   │   │   └── com/codecool/minishop/

│   │   │       ├── config/

│   │   │       ├── controller/

│   │   │       ├── dto/

│   │   │       ├── model/

│   │   │       ├── repository/

│   │   │       └── service/

│   │   │

│   │   └── resources/

│   │       ├── static/

│   │       │   └── css/

│   │       ├── templates/

│   │       ├── META-INF/

│   │       └── application.properties

│   │

│   └── test/

│       └── java/

│

├── .env.example

├── .gitignore

├── compose.yaml

├── pom.xml

└── README.md

```



\### Architecture Overview



\- \*\*Controller layer:\*\* Handles HTTP requests and responses.

\- \*\*Service layer:\*\* Contains application logic and business rules.

\- \*\*Repository layer:\*\* Handles database operations through Spring Data JPA.

\- \*\*Model layer:\*\* Contains entities and domain objects.

\- \*\*DTO layer:\*\* Transfers data between application layers and the web interface.

\- \*\*Config layer:\*\* Contains application configuration and security settings.

\- \*\*Templates:\*\* Contains Thymeleaf HTML pages.

\- \*\*Static resources:\*\* Contains CSS and other frontend assets.



\---



\# Future Improvements



Possible future extensions include:



\- Shopping cart functionality.

\- Payment provider integration.

\- Product image uploads.

\- Additional product filtering options.

\- Deployment to a cloud hosting platform.



\---



\# Author



\*\*Tomey SNR\*\*



GitHub: \[Tomey-SNR](https://github.com/Tomey-SNR)



\---

