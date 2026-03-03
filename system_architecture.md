# FixSure — Service Booking App
## System Architecture Document

**Version:** 1.0  
**Date:** 2026-03-02  
**Status:** Draft

---

## 1. High-Level Architecture Overview

FixSure follows a **3-tier client-server architecture** with a mobile/web frontend, a RESTful backend API layer, and a managed data layer. External services handle payment processing and notifications.

```
┌──────────────────────────────────────────────┐
│              CLIENT LAYER                    │
│  ┌──────────────┐    ┌────────────────────┐  │
│  │  Mobile App  │    │  Web Prototype     │  │
│  │(React Native)│    │  (React/Vite)      │  │
│  └──────┬───────┘    └────────┬───────────┘  │
└─────────┼───────────────────┼───────────────┘
          │ HTTPS / REST API   │
┌─────────▼───────────────────▼───────────────┐
│              API GATEWAY LAYER               │
│  (Rate Limiting · Auth · Load Balancing)     │
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│               BACKEND SERVICES               │
│  ┌────────────┐ ┌──────────┐ ┌───────────┐  │
│  │ Auth       │ │ Booking  │ │ Payment   │  │
│  │ Service    │ │ Service  │ │ Service   │  │
│  └────────────┘ └──────────┘ └───────────┘  │
│  ┌────────────┐ ┌──────────┐ ┌───────────┐  │
│  │ Service    │ │ User     │ │ Notif.    │  │
│  │ Catalog    │ │ Profile  │ │ Service   │  │
│  └────────────┘ └──────────┘ └───────────┘  │
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│               DATA LAYER                     │
│  ┌──────────────┐    ┌──────────────────┐   │
│  │  PostgreSQL  │    │  Redis Cache     │   │
│  │  (Primary DB)│    │  (Sessions/Slots)│   │
│  └──────────────┘    └──────────────────┘   │
│  ┌──────────────┐    ┌──────────────────┐   │
│  │  S3 / Cloud  │    │  Firebase (FCM)  │   │
│  │  Storage     │    │  Push Notifs     │   │
│  └──────────────┘    └──────────────────┘   │
└─────────────────────────────────────────────┘
```

---

## 2. Frontend Architecture (Web Prototype)

The web prototype is a **single-page application (SPA)** built with React + Vite, designed as a mobile-viewport interactive demo of the full app flow.

```
src/
├── App.jsx                  # Root state machine (screen router)
├── index.css                # Global design system & CSS variables
├── components/
│   ├── BottomNav.jsx        # Tab navigation bar
│   ├── TopBar.jsx           # Screen header with back navigation
│   └── ServiceCard.jsx      # Reusable service listing card
├── screens/
│   ├── HomeScreen.jsx       # Landing screen with categories
│   ├── ServicesScreen.jsx   # Services listing & filter
│   ├── ServiceDetailScreen.jsx  # Single service view
│   ├── BookingSlotScreen.jsx    # Date + time slot selection
│   ├── CheckoutScreen.jsx       # Payment method & order summary
│   ├── ConfirmationScreen.jsx   # Success confirmation
│   ├── HistoryScreen.jsx        # Past & upcoming bookings
│   └── ProfileScreen.jsx        # User profile management
└── data/
    └── services.js          # Mock service catalog data
```

---

## 3. Backend Microservices

### 3.1 Auth Service
- **Responsibilities:** OTP generation, JWT issuance/refresh, session management.
- **Stack:** Node.js + Express, Redis for OTP TTL.
- **Endpoints:** `POST /auth/send-otp`, `POST /auth/verify-otp`, `POST /auth/refresh`.

### 3.2 Service Catalog Service
- **Responsibilities:** CRUD for services, categories, and pricing.
- **Stack:** Node.js + Express, PostgreSQL.
- **Endpoints:** `GET /services`, `GET /services/:id`, `GET /categories`.

### 3.3 Booking Service
- **Responsibilities:** Slot management, booking creation, status updates, technician assignment.
- **Stack:** Node.js + Express, PostgreSQL (bookings), Redis (slot locking).
- **Endpoints:** `GET /slots?serviceId&date`, `POST /bookings`, `GET /bookings/:id`, `PATCH /bookings/:id/cancel`.

### 3.4 Payment Service
- **Responsibilities:** Order creation, payment gateway integration, webhook handling, refund management.
- **Stack:** Node.js + Express, integrated with Razorpay.
- **Supports:** Cash (marked pending), Card (gateway), UPI (gateway).
- **Endpoints:** `POST /payment/create-order`, `POST /payment/verify`, `POST /payment/refund`.

### 3.5 Notification Service
- **Responsibilities:** Push notifications, SMS alerts, email confirmations.
- **Stack:** Node.js consumer, Firebase FCM (push), Twilio (SMS), SendGrid (email).

### 3.6 User Profile Service
- **Responsibilities:** User data management, saved addresses, notification preferences.
- **Endpoints:** `GET /users/me`, `PUT /users/me`, `GET /users/addresses`, `POST /users/addresses`.

---

## 4. Data Models

### 4.1 User
```json
{
  "id": "uuid",
  "name": "string",
  "phone": "string",
  "email": "string",
  "profilePhotoUrl": "string",
  "addresses": ["Address"],
  "createdAt": "timestamp"
}
```

### 4.2 Service
```json
{
  "id": "uuid",
  "categoryId": "uuid",
  "name": "string",
  "description": "string",
  "imageUrl": "string",
  "basePrice": "number",
  "durationMinutes": "number",
  "rating": "number",
  "reviewCount": "number",
  "includes": ["string"]
}
```

### 4.3 Booking
```json
{
  "id": "uuid",
  "userId": "uuid",
  "serviceId": "uuid",
  "scheduledDate": "date",
  "slotId": "uuid",
  "addressId": "uuid",
  "status": "enum(PENDING|CONFIRMED|IN_PROGRESS|COMPLETED|CANCELLED)",
  "technicianId": "uuid | null",
  "paymentId": "uuid",
  "totalAmount": "number",
  "createdAt": "timestamp"
}
```

### 4.4 Payment
```json
{
  "id": "uuid",
  "bookingId": "uuid",
  "method": "enum(CASH|CARD|UPI)",
  "status": "enum(PENDING|SUCCESS|FAILED|REFUNDED)",
  "gatewayOrderId": "string",
  "gatewayPaymentId": "string",
  "amount": "number",
  "paidAt": "timestamp | null"
}
```

---

## 5. Booking Flow Sequence

```
User          App          Booking Svc       Payment Svc       Razorpay
 │             │                │                 │                │
 │ Select Slot │                │                 │                │
 │────────────▶│ GET /slots     │                 │                │
 │             │───────────────▶│                 │                │
 │             │◀───────────────│                 │                │
 │ Book Now    │                │                 │                │
 │────────────▶│ POST /bookings │                 │                │
 │             │───────────────▶│                 │                │
 │             │◀── booking_id ─│                 │                │
 │             │                │ POST /create-order              │
 │             │─────────────────────────────────▶│               │
 │             │                │                 │──────────────▶│
 │             │                │                 │◀── order_id ──│
 │             │◀── order_id ───────────────────  │               │
 │ Pay         │                │                 │               │
 │────────────▶│ POST /verify   │                 │               │
 │             │─────────────────────────────────▶│               │
 │             │                │                 │ Verify sig.   │
 │             │◀── SUCCESS ───────────────────── │               │
 │ Confirmation│                │                 │               │
 │◀────────────│                │                 │               │
```

---

## 6. Technology Stack Summary

| Layer | Technology |
|-------|-----------|
| Mobile App | React Native (planned) |
| Web Prototype | React 18 + Vite |
| Styling | Vanilla CSS (custom design system) |
| Backend Runtime | Node.js 20 LTS |
| Backend Framework | Express.js |
| Primary Database | PostgreSQL 16 |
| Cache / Sessions | Redis 7 |
| Payment Gateway | Razorpay |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| SMS | Twilio |
| Email | SendGrid |
| File Storage | AWS S3 / Cloudinary |
| API Gateway | AWS API Gateway / Nginx |
| Hosting (API) | AWS ECS / Railway |
| Hosting (Frontend) | Vercel / Netlify |

---

## 7. Security Architecture

- All traffic encrypted via **TLS 1.3**.
- JWT tokens signed with **RS256** (asymmetric keys).
- OTPs expire in **5 minutes** and are single-use.
- Payment signatures verified using **HMAC-SHA256** with Razorpay secret.
- API rate limiting: 60 req/min per IP on public endpoints.
- Database connections use **pgBouncer** connection pooling.
- All user PII fields encrypted at rest using **AES-256**.
