# Louver

An Android car rental and booking application built with Java and Room, following MVVM architecture.

## Overview

Louver is a fully offline Android application that simulates a real-world car showroom and rental system. Users can browse a catalogue of cars, filter by category and availability, book rentals, and track their booking history. The app handles the full rental lifecycle — from browsing and favoriting cars, to booking with automatic price calculation, to receiving local notifications when a booking is about to end or becomes overdue.

Authentication is built in, with separate user and admin roles. Admins have access to a dashboard for managing the car catalogue. All data is stored locally using Room (SQLite), with no external backend required.

## Tech Stack

- Java
- Android SDK (minSdk 24, targetSdk 36)
- MVVM + Repository Pattern
- Room (local database)
- LiveData
- ViewBinding
- AlarmManager (local notifications)
- Material Design components