# Movies App

A modern Android application that allows users to browse and search for movies using The Movie Database (TMDB) API. Built with clean architecture principles and modern Android development tools.

## Features

- Browse popular movies
- Search movies by title
- Infinite scrolling pagination
- Modern Material 3 UI
- Responsive grid layout
- Movie details view
- Error handling

## Tech Stack

- **Language**: Kotlin
- **Architecture**: Clean Architecture (Domain, Data, Presentation layers)
- **UI**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Coroutines**: For asynchronous operations
- **Networking**: Retrofit
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation Compose
- **State Management**: StateFlow
- **API**: The Movie Database (TMDB)

## Project Structure

```
app/
├── data/
│   ├── model/           # Data models
│   ├── remote/          # API service and network related code
│   └── repository/      # Repository implementations
├── domain/
│   ├── model/           # Domain models
│   ├── repository/      # Repository interfaces
│   └── usecase/         # Use cases
├── di/                  # Dependency injection modules
├── presentation/
│   ├── movies/          # Movie list screen
│   ├── details/         # Movie details screen
│   └── navigation/      # Navigation components
└── ui/
    └── theme/           # App theme and styling
```

## Architecture

The app follows Clean Architecture principles with three main layers:

1. **Domain Layer**
   - Contains business logic
   - Defines repository interfaces
   - Implements use cases
   - Independent of Android framework

2. **Data Layer**
   - Implements repository interfaces
   - Handles data operations
   - Contains API service and data models
   - Manages data sources

3. **Presentation Layer**
   - Contains UI components
   - Implements MVVM pattern
   - Uses Jetpack Compose for UI
   - Handles user interactions

## Key Components

### Use Cases
- `GetPopularMoviesUseCase`: Fetches popular movies with pagination
- `SearchMoviesUseCase`: Searches movies by query with pagination

### Repository
- `MovieRepository`: Interface defining movie data operations
- `MovieRepositoryImpl`: Implementation handling API calls

### ViewModel
- `MovieListViewModel`: Manages movie list state and user interactions
- Handles pagination, search, and error states

### UI
- `MovieListScreen`: Displays movie grid with search functionality
- `MovieDetailsScreen`: Shows detailed movie information
- Material 3 design components
- Responsive grid layout
- Error handling UI
