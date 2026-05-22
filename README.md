# FlexiPay — Buy Now, Pay Later Platform

## Why This Project Matters

Access to credit shouldn't be complicated. FlexiPay is a modern **Buy Now, Pay Later (BNPL)** platform built to bridge the gap between instant purchasing power and responsible financial management. Instead of upfront payments or traditional credit cards, users can split purchases into affordable monthly EMIs — with interest rates dynamically tied to their creditworthiness.

What sets FlexiPay apart is its **intelligent credit engine**: every user gets a real-time credit score, every loan application goes through automated risk assessment, and every repayment updates the user's financial profile. This creates a self-improving credit cycle — responsible borrowers are rewarded with better rates over time.

Whether you're a first-time borrower building credit history or a merchant looking to offer flexible payment options, FlexiPay provides the infrastructure to make it happen.

---

## How the Tech Stack Powers the Platform

### Java 17 + Spring Boot — The Financial Brain
Spring Boot serves as the backbone of all business logic. Its mature ecosystem was the right choice for handling the complexity of financial workflows — from order creation and risk validation to EMI schedule generation and payment tracking. Java 17's strong typing and performance make it reliable for operations where correctness is non-negotiable.

### Spring Security + JWT — Zero-Trust Authentication
Every API call is protected. Spring Security enforces role-based access (Customer, Merchant, Admin), while JWT tokens ensure stateless, scalable authentication. Passwords are BCrypt-hashed — no plain-text credentials ever touch the database.

### PostgreSQL + Spring Data JPA + Flyway — A Reliable Financial Ledger
Financial data demands reliability. PostgreSQL provides ACID-compliant transactions so no payment or credit event is ever lost mid-operation. Flyway manages schema migrations as versioned SQL scripts, ensuring the database evolves safely across environments. JPA abstracts the data layer cleanly, keeping business logic free from raw SQL.

### React + Vite — Fast, Focused UI
The frontend is intentionally lightweight — React handles dynamic state (auth flow, form feedback, dashboard rendering) while Vite ensures near-instant hot reloads during development and optimized production bundles. Axios with a JWT interceptor means authentication is invisible to the user but always enforced.

---

## Core Capabilities at a Glance

| Capability | How It Works |
|---|---|
| Smart Credit Scoring | Weighted model (payment history, debt ratio, credit age) updates dynamically after every transaction |
| Risk-Based Pricing | Interest rates (0% → 24%) assigned automatically based on credit score at the time of application |
| EMI Engine | Reducing-balance formula generates a full amortization schedule for any tenure |
| Secure Auth | JWT tokens with 24-hour expiry, BCrypt hashing, role-gated endpoints |
| Audit Trail | Every credit event (payment, default, new loan) is logged to `credit_history` for transparency |

---

## Tech Stack Summary

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Authentication | JWT (JJWT 0.12.3), BCrypt |
| Database | PostgreSQL, Spring Data JPA, Flyway |
| Frontend | React 19, Vite, React Router, Axios |
| Build Tools | Maven (backend), npm (frontend) |
