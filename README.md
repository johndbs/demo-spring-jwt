# SpringBoot REST API with JWT Security

## Overview
This project demonstrates a REST API using SpringBoot, secured with JWT. It's designed to showcase best practices in API development with a focus on security and efficiency.

## Features
- REST API implementation using SpringBoot.
- JWT based security for enhanced protection.

## Technologies Used
- SpringBoot
- SpringData
- H2 Database
- Lombok
- io.jsonwebtoken

## Getting Started

### Accessing the H2 Console
The H2 console is accessible at `http://localhost:8080/h2-console`. You can use this to directly interact with the in-memory H2 database.

### Examples and How to Use
In the `bruno` folder, you will find examples to call the REST controllers with [Bruno](https://www.usebruno.com/).

Here's a quick guide on getting started:

1. **Create a User:** Begin by creating a user with the POST `/signup` endpoint.
2. **Authenticate:** Next, ask for a token with POST `/signin`. This will authenticate the user and return a JWT token.
3. **Access Secured Endpoints:** Copy the token value contained in the POST `/signin` response and paste it in the Auth tab of the `hello` or `say-hello` case to access secured endpoints.

## Security
This project uses JWT (JSON Web Token) for securing the REST API. Ensure you keep your tokens secure and do not expose them in public repositories or client-side applications.