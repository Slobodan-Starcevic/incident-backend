
---

# Incident App – Backend

This repository contains the **Java Spring Boot backend** for the **Incident App**, a school project in collaboration with **Innovactions**.

The backend is responsible for handling incidents and exposing APIs for different integrations (Slackbot, WhatsApp, email, and more).

## Project Context

* **Company:** Innovactions
* **Type:** School project
* **Goal:** Build a prototype incident management system that integrates with real communication tools.
* **Phase:** MVP (hack together a first working version to validate direction and gather feedback).

## Tech Stack

* [Java 21 Eclipse Temurin](https://adoptium.net/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/) (dependency management & build)
* Database: PostgreSQL
* Messaging/Integrations: Slack, WhatsApp, Email

## Planned Integrations

* **Slackbot**: Allow users to report and follow up incidents directly from Slack.
* **WhatsApp**: Future extension for incident notifications or reporting.
* **Email**: For alerts, summaries, or fallback communication.

### Prerequisites

* Install **Java 21**
* Install **Maven 3.8+**
* Docker

## Development Notes

* **MVP focus:** hack together something functional, don’t over-engineer yet.
* Code quality and structure will evolve as we learn what works best.
* Future phases may add:

    * Persistent database storage
    * More integrations
    * More complex determination system (AI possible)

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
