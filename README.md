Let's build a web system to track the carbon footprint of scientific workflows
Requirements
-Backend Requirements
-Java 17+
-Maven 3.6+

-Frontend Requirements
-Node.js 16+
-npm 8+

Installation & Running:
-Backend Startup
-Navigate to the backend directory
-Package the application using Maven
cd frontend
mvn clean package
mvn spring-boot:run
or
java -jar target/carbon_footprint-1.0.0.jar
-Backend service runs at: http://localhost:8080

-Frontend Startup
-Navigate to the frontend directory
cd frontend
Install dependencies
npm install
npm run dev
-Frontend application runs at: http://localhost:5173
