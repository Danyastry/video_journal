## Video Journal ##

## Features

- Record videos using the device camera
- Add optional descriptions to videos
- View videos in a scrollable feed (newest first)
- Play videos inline
- Generate video thumbnails
- Share videos with other apps
- Delete videos

## Architecture

This app follows Clean Architecture principles with three main layers:

- **Presentation**: UI components, ViewModels, and UI state management
- **Domain**: Business logic, use cases, and domain models
- **Data**: Data sources, repositories, and data mapping

## Technologies Used

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit for Android
- **Media3 ExoPlayer** - Video playback
- **CameraX** - Camera functionality for video recording
- **SQLDelight** - SQL database with type-safe Kotlin APIs
- **Koin** - Dependency injection
- **Coil** - Image loading for thumbnails
- **Accompanist Permissions** - Permission handling

## Testing

The app includes unit tests for ViewModels and use cases. Run the tests with:

```
./gradlew test
```
