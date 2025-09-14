TEST-bk

# PUBG Project

This project is a web application designed to display PUBG (PlayerUnknown's Battlegrounds) player and match statistics. It consists of a Spring Boot backend and a React frontend.

## Features

- Search for PUBG players
- View player statistics
- View match details

## Technologies Used

### Backend (Spring Boot)

- Java 17
- Spring Boot
- Gradle
- PUBG API integration

### Frontend (React)

- React.js
- Node.js
- npm
- Axios for API calls
- React Router for navigation
- Bootstrap for styling

## Setup and Installation

### Prerequisites

- Java 17 or higher
- Node.js and npm
- Gradle

### Backend Setup

1.  Navigate to the project root directory.
2.  Build the Spring Boot application:
    ```bash
    ./gradlew clean build
    ```
3.  Run the application:
    ```bash
    ./gradlew bootRun
    ```
    The backend server will start on `http://localhost:8080`.

### Frontend Setup

1.  Navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the React development server:
    ```bash
    npm start
    ```
    The frontend application will be available at `http://localhost:3000`.

## API Key Configuration

To run this application, you will need a PUBG API key.
1.  Obtain a PUBG API key from the [PUBG Developer Portal](https://developer.pubg.com/).
2.  Add your API key to the `application.properties` file in the `src/main/resources` directory of the backend:
    ```properties
    pubg.api.key=YOUR_API_KEY_HERE
    ```

## Project Structure

```
.
├── build.gradle
├── gradlew
├── README.md
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── services/
│   │   └── styles/
│   └── package.json
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/pubg/
│   │       ├── controller/
│   │       ├── dto/
│   │       └── service/
│   └── resources/
│       ├── application.properties
│       └── templates/
    └── test/
```