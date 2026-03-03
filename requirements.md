# FixSure — Service Booking App
## Requirements Specification Document

**Version:** 1.0  
**Date:** 2026-03-02  
**Status:** Draft

---

## 1. Overview

**FixSure** is a mobile-first service booking application that allows users to book household and utility services such as AC maintenance, RO servicing, CCTV installation, house cleaning, and more. The app provides a seamless end-to-end experience from service discovery to booking confirmation, with multiple payment options.

---

## 2. Stakeholders

| Role | Description |
|------|-------------|
| Customer (End User) | Books services via the mobile app |
| Service Provider / Technician | Fulfills assigned service requests |
| Admin | Manages service catalog, pricing, and technician allocation |

---

## 3. Functional Requirements

### 3.1 Authentication
- FR-01: User can register with name, phone number, and email.
- FR-02: User can log in with phone number and OTP.
- FR-03: User can log out from the Profile screen.
- FR-04: Auth session persists across app restarts.

### 3.2 Home Screen
- FR-05: Display a personalized greeting with user's first name.
- FR-06: Show a search bar to quickly find services.
- FR-07: Display service category cards (AC, RO, CCTV, Cleaning, Plumbing, etc.).
- FR-08: Show a promotional banner carousel.
- FR-09: Display "Popular Services" and recently booked services sections.

### 3.3 Services Screen
- FR-10: List all available service categories.
- FR-11: Filter services by category, rating, or price.
- FR-12: Show service name, thumbnail, average rating, and starting price.

### 3.4 Service Details Screen
- FR-13: Display service title, description, what's included, and price.
- FR-14: Show average rating and number of reviews.
- FR-15: Display estimated duration of the service.
- FR-16: Provide a "Book Now" call-to-action button.

### 3.5 Booking Flow
- FR-17: User can select a preferred date from a calendar view.
- FR-18: User can select an available time slot (Morning / Afternoon / Evening).
- FR-19: User must provide a service address.
- FR-20: User can review order summary before proceeding to payment.

### 3.6 Payment
- FR-21: Support three payment methods: **Cash**, **Card**, and **UPI**.
- FR-22: Show itemized price breakdown (service cost + taxes + platform fee).
- FR-23: On successful payment, generate a booking confirmation with a unique Booking ID.
- FR-24: On payment failure, show an error screen with retry option.

### 3.7 Confirmation Screen
- FR-25: Show a success animation post-payment.
- FR-26: Display booking ID, service name, scheduled date/time, and assigned technician name (if available).
- FR-27: Allow user to add the booking to their calendar.
- FR-28: Provide options to "Track Booking" or "Go to Home".

### 3.8 History Screen
- FR-29: List all past and upcoming bookings.
- FR-30: Each booking shows status: Pending / Confirmed / In Progress / Completed / Cancelled.
- FR-31: User can view full details of any past booking.
- FR-32: User can cancel an upcoming booking (subject to cancellation policy).
- FR-33: User can rebook a completed service.

### 3.9 Profile Screen
- FR-34: Display user's name, email, phone number, and profile photo.
- FR-35: Allow user to edit profile information.
- FR-36: Show saved addresses.
- FR-37: Show saved payment methods.
- FR-38: Provide links to Help & Support, Privacy Policy, and Terms of Service.
- FR-39: Show notification preferences.

---

## 4. Non-Functional Requirements

### 4.1 Performance
- NFR-01: App should load the home screen within 2 seconds on a standard 4G connection.
- NFR-02: API response time for service listing should be under 500ms.

### 4.2 Usability
- NFR-03: The UI must be accessible and usable one-handed on a 5–6.5 inch screen.
- NFR-04: All interactive elements must have a minimum touch target of 44×44px.
- NFR-05: The booking flow must require no more than 4 taps from service selection to confirmation.

### 4.3 Reliability
- NFR-06: The system should have 99.9% uptime for the booking and payment services.
- NFR-07: Payment processing must handle network interruptions gracefully.

### 4.4 Security
- NFR-08: All API communication must use HTTPS/TLS 1.3.
- NFR-09: Payment data must be handled via a PCI-DSS compliant gateway (e.g., Razorpay, Stripe).
- NFR-10: User PII must be encrypted at rest.

### 4.5 Compatibility
- NFR-11: Support Android 10+ and iOS 14+.
- NFR-12: Web prototype must render correctly in Chrome, Firefox, and Safari.

---

## 5. Constraints

- The app must follow Indian UPI payment standards and integrate with a UPI-compatible gateway.
- Service availability is geo-restricted by pin code.
- Cash on Service (CoS) is only available for bookings under ₹5,000.

---

## 6. Out of Scope (v1.0)

- Real-time technician tracking on map.
- In-app chat with technician.
- Multi-language support (Hindi, regional languages) — planned for v1.1.
- Subscription / Annual Maintenance Contracts (AMC) — planned for v2.0.

---

## 7. Assumptions

- Users have a stable internet connection for booking.
- Service providers are already onboarded separately via an admin portal.
- Pricing is pre-configured by admin; dynamic pricing is not in v1.0 scope.
