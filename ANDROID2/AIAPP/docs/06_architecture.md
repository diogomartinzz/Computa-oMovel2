# Architecture

Architecture: MVVM (Model-View-ViewModel)

Layers:

UI -> ViewModel -> Repository -> API Service

- **UI**: `MainActivity`, XML Layouts, RecyclerView Adapter
- **ViewModel**: `MainViewModel` (handles UI state and interacts with Repository)
- **Repository**: `ImageRepository` (abstracts data fetching)
- **API Service**: Retrofit interface for network requests
