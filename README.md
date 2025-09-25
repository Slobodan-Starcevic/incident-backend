
---

# Incident App – Backend

This repository contains the **Java Spring Boot backend** for the **Incident App**, a school project in collaboration with **Innovactions**.

The backend is responsible for handling incidents and exposing APIs for different integrations (Slackbot, WhatsApp, email, and more).

## Project Context

* **Company:** Innovactions
* **Type:** School project
* **Goal:** Build an incident management system that integrates with real communication tools.
* **Phase:** V1, expand the Slack integration, integrate WhatsApp and thoroughly test the system.

## Tech Stack

* [Java 21 Eclipse Temurin](https://adoptium.net/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/) (dependency management & build)
* Database: PostgreSQL
* Messaging/Integrations: Slack, WhatsApp, Email

## Integrations

* **Slackbot**: Allow users to report and follow-up incidents directly from Slack.
* **WhatsApp**: Future extension for incident notifications or reporting.

### Prerequisites

* Install **Java 21**
* Install **Maven 3.8+**
* Docker

## Development Notes

* MVP is implemented and functional
* Slack integration with dynamic channels complete
* Focus on WhatsApp integration
* Implement CI/CD with dockerization
* Test existing code and make sure new code is always tested before merging

## Contributors

* Fontys student team
  * Robin Hannan
  * Joost Raemakers
  * Yirgalem Hailemariam
  * Slobodan Starcevic
  * Matthijs Hulshof
* Fontys mentors
  * Erik Schriek
  * Nicole Zuurbier-Munneke
* Innovactions stakeholders
  * Steven van 't Klooster

---
