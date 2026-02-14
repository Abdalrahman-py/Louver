# Louver – Architecture Documentation

## Overview

Louver is an Android car rental application built using:

- Java
- MVVM Architecture
- Room Database
- ViewBinding
- Fragments (Single-Activity Architecture)
- LiveData
- RecyclerView
- Local Notifications

The application simulates a real-world car showroom booking system, including browsing, filtering, booking, favorites, notifications, and user management.

Project requirements are based on the official course specification.

## Architecture Pattern

The application follows MVVM with Repository Pattern:

UI (Fragments)
↓
ViewModel
↓
Repository
↓
Room (DAO + Entities)

### Rules

1. Fragments must never access DAO directly.
2. All business logic must be inside Repository or dedicated manager classes.
3. ViewModels must not hold Android Context.
4. Entities must not contain business logic.
5. Time and price calculations must be centralized.

## Project Structure

com.example.louver
│
├── data
│   ├── auth
│   ├── converter
│   ├── dao
│   ├── db
│   ├── entity
│   ├── relation
│   ├── repository
│   ├── seed
│
├── ui
│   ├── auth
│   ├── home
│   ├── booking (future)
│   ├── profile (future)
│   ├── favorites (future)
│   ├── settings (future)
│   ├── MainActivity

## Data Layer

### Entities

- AppSettingsEntity
- BookingEntity
- CarEntity
- CarImageEntity
- CategoryEntity
- FavoriteEntity
- NotificationEntity
- ReviewEntity
- UserEntity

### DAO

DAO classes provide database access.
They must:
- Use LiveData for observable queries.
- Use @Transaction for relational queries.
- Avoid business logic.

### Repository

Repositories:
- Abstract DAO calls.
- Handle validation.
- Handle booking rules.
- Handle availability updates.
- Handle favorites logic.
- Handle user authentication.

## Booking Rules

1. pickup time must be before return time.
2. minimum duration is 1 day.
3. car must be available.
4. final price = number_of_days * daily_price.

All booking calculations must be centralized in:
BookingCalculator (inside repository layer).

## Notifications

System must support:

- Notification 1 hour before booking ends.
- Notification at booking end.
- Notification if car return is late.

Notification scheduling must be separated from UI.

## UI Layer

- Single Activity (MainActivity)
- Multiple Fragments
- ViewBinding enabled
- Each major screen has its own ViewModel.

Fragments must:
- Observe LiveData
- Not contain business logic
- Not directly access database

## Authentication

- Email + Password login
- Registration
- Login state stored in AppSettingsEntity
- Passwords must be hashed

## Coding Standards

Naming:

- Entity: XxxEntity
- DAO: XxxDao
- Repository: XxxRepository
- ViewModel: XxxViewModel
- Fragment: XxxFragment

Prohibited:

- DAO calls inside Fragments
- Business logic in XML
- Price calculation inside UI
- Direct database calls inside ViewModel

## Definition of Done

A feature is complete when:

- UI works correctly
- Data persists in Room
- Survives configuration change
- No crashes
- Follows architecture rules