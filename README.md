# Event Lottery System (Android mobile application)

## Overview

The **Event Lottery System** is an Android application designed to provide a fair and accessible way for users to register for high-demand community events. Instead of first-come-first-served registration, the app uses a **lottery-based waiting list** that gives all interested entrants an equal chance to participate.

The system supports multiple user roles (Entrant, Organizer, Administrator) and handles the full event lifecycle: creation, registration periods, lottery selection, acceptance/decline flows, replacement draws, and final enrollment.

This project was developed as a team-based, agile software engineering project and completed through multiple checkpoints, culminating in a live demo-ready prototype.

---

## User Interface
<p align="center">
<img width="270" height="600" alt="image" src="https://github.com/user-attachments/assets/c4eb7c9b-a2e5-467c-bd91-f777c3c0f4d1" /> 
<img width="270" height="600" alt="image" src="https://github.com/user-attachments/assets/f0eab821-d8e5-4029-8f01-56517bc3ac79" /> 
<img width="270" height="600" alt="image" src="https://github.com/user-attachments/assets/8555eee3-83fc-417d-a9c5-cc7b2cc9ae35" />
</p>

---

## Key Features
- Lottery-based waiting list system
- QR code scanning to access and join events
- Multi-role support (entrants and organizers)
- Event creation with registration windows and capacity limits
- Random sampling with replacement logic
- Notifications for lottery results
- Firebase-backed real-time data storage

---

## Technology Stack

* **Language:** Java
* **Platform:** Android
* **Backend:** Firebase Firestore
* **Authentication:** Device-based identification (no username/password)
* **Storage:** Firebase Storage (event posters)
* **Version Control:** GitHub

---

## Challenging Parts

* Coordinating Firebase reads/writes to avoid race conditions
* Integrating notifications with changing event states
* Handling edge cases such as declines, cancellations, and replacement draws
* Implementing geolocation capture and visualizing entrant locations on maps
* Implementing QR code generation and scanning to reliably deep-link users to the correct event within the app

---

## What We Learned

* How to design and implement a multi-user system with real-time data updates
* Applying object-oriented design principles in a large Android codebase
* Managing asynchronous operations and state in Firebase-backed applications
* Collaborating effectively in a team using GitHub and Agile-style workflows
* Translating user stories into working features and handling real-world edge cases

