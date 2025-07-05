# WelcomeWave - Virtual Receptionist Kiosk

WelcomeWave is a fully functional, tablet-based virtual receptionist application built with modern Android development practices. It allows visitors to check in, select an employee to visit, and automatically notifies that employee via email. The app also features a complete, password-protected admin section for managing the employee list.

## Features

### Guest Flow
- **Dynamic Welcome Screen:** An animated, full-screen video background with a dynamic greeting that changes based on the time of day.
- **Employee Selection:** A searchable list of employees with photos and titles.
- **Visitor Details Form:** A multi-guest check-in form that captures visitor names and company.
- **Automatic Email Notification:** Securely sends a formatted HTML email to the selected employee upon check-in.
- **Confirmation & Auto-Reset:** A success screen that automatically returns to the welcome screen for the next guest.
- **Check-out System:** A separate flow for guests to check themselves out, logging the time.

### Admin Flow
- **Secure Access:** Hidden gesture on the welcome screen leads to a PIN-protected login page.
- **Employee Management (CRUD):** Full Create, Read, Update, and Delete functionality for the employee list.
- **Photo Picker Integration:** Uses the modern Android Photo Picker to select and save employee photos permanently.
- **Visitor Log:** A screen to view a complete history of all check-ins and check-outs.

## Tech Stack
- **UI:** 100% Jetpack Compose with Material 3 design.
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Hilt
- **Local Database:** Room
- **Networking:** Retrofit
- **Image Loading:** Coil
- **Backend:** Firebase Functions (Node.js) with Google Cloud Secret Manager.
- **Email Service:** Brevo (formerly Sendinblue)
