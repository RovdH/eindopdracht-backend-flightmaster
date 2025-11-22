# Flight Master Web-API
## Installatiehandleiding

### Inhoudsopgave
1. [Inleiding](#inleiding)
2. [Beschrijving van de web-API](#beschrijving-van-de-web-api)
3. [Projectstructuur en gebruikte technieken](#projectstructuur-en-gebruikte-technieken)
4. [Benodigdheden](#benodigdheden)
5. [Installatie-instructies (stappenplan)](#installatie-instructies-stappenplan)
6. [Configuratie en omgevingsvariabelen](#configuratie-en-omgevingsvariabelen)
7. [Tests uitvoeren](#tests-uitvoeren)
8. [Gebruikers en autorisatieniveaus](#gebruikers-en-autorisatieniveaus)
9. [Default Gebruikers](#default-gebruikers)
10. [Stappenplan na eerste login](#stappenplan-na-eerste-login)
11. [Bronnen](#bronnen)

---

## Inleiding
De **Flight Master REST API** is ontwikkeld om **helikopterrondvluchten-events** te beheren.  
De applicatie vormt de backend van een boekingssysteem waarbij gebruikers zich kunnen registreren en inschrijven voor vluchten. Administrators kunnen evenementen, helikopters en passagiers beheren, terwijl het systeem automatisch tankstops berekent en passagierslijsten als PDF genereert.

### Belangrijkste functionaliteiten
- Registreren en inloggen users (met JWT-beveiliging en rol gebaseerde rechten).
- Inschrijven van passagiers voor vluchten, inclusief gewichtscontrole en capaciteit-validatie.
- Beheer van evenementen, helikopters en geplande vluchten door admins.
- Automatisch genereren en downloaden van passagierslijsten in PDF.
- Volledige CRUD-operaties via REST-endpoints (GET, POST, PUT, DELETE).

---

## Projectstructuur en gebruikte technieken
De applicatie is opgebouwd volgens de **MVC-architectuur** van Spring Boot en maakt gebruik van Controllers voor de endpoints, DTO’s om data te valideren en vervuiling van de database te voorkomen, Services waar de business logica te vinden is en Repositories voor de communicatie naar de database.

### Gebruikte technieken
- **Java 21 LTS**
- **JDK Coretto 21**
- **Spring Boot 3.x**
- **Spring Web / JPA / Security / Validation**
- **Maven**
- **H2 Database**
- **JUnit 5 / Mockito**
- **JWT**
- **Lombok**

### Project structuur
```
src
 ├── main
 │   ├── java/nl/helicenter/flightmaster
 │   │    ├── controller  
 │   │    ├── dto            
 │   │    ├── model          
 │   │    ├── repository 
 │   │    ├── service       
 │   │    └── security  
 │   └── resources
 │        ├── application.properties
 │        ├── data.sql       
 │        └── schema.sql     
 └── test                    
```

---

## Benodigdheden
| Software | Versie | Omschrijving |
|-----------|---------|---------------|
| Java JDK | ≥ 21 LTS | Nodig om Spring Boot te draaien |
| Maven | ≥ 3.8 | Voor het bouwen en testen |
| Git | - | Om de repo te klonen |
| Postman of browser | – | Voor het testen van endpoints |

**Let op:** Je hoeft Maven niet apart te installeren. Dit project bevat de **Maven Wrapper** (`mvnw` / `mvnw.cmd`). Volg daarom gewoon de installatie-instructies.

---

## Installatie-instructies (stappenplan)
### Gebruik altijd de Maven Wrapper
- **macOS/Linux:** gebruik `./mvnw HIER JE COMMAND`
- **Windows (PowerShell/CMD):** gebruik `.\mvnw.cmd HIER JE COMMAND`


1. **Project downloaden of klonen**
   ```bash
   git clone https://github.com/RovdH/eindopdracht-backend-flightmaster.git
   cd eindopdracht-backend-flightmaster
   ```
2. **Controleer of Java werkt**
   ```bash
   java -version
   ```
3. **Controleer of Maven werkt**
   ```bash
   ./mvnw -v
   ```

4. **Controleer of je in de **main** branche zit**
   ```bash
   Git branch -v
   ```

3. **Dependencies downloaden en builden**
   ```bash
   ./mvnw clean install
   ```
4. **Applicatie starten**
   ```bash
   ./mvnw spring-boot:run
   ```
   De server start standaard op **http://localhost:8080**.
5. **Test via browser of Postman**  
   Open [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)  
   → Verwachte output: `{"status":"UP"}`

---

## Configuratie en omgevingsvariabelen
Standaard werkt de app zonder extra configuratie dankzij `application.properties`.  
Instellingen zoals:

```properties
spring.datasource.url=jdbc:h2:mem:flightmaster
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
jwt.secret=SecretKeyForJWTGeneration
jwt.expiration=3600000
```
**Controleer en match de database settings**
In de application.properties vind je ook de huidige database connectie gegevens. Gebruik deze voor je eigen database of pas deze aan naar eigen voorkeur.


**Optioneel:** wijzig de `jwt.secret` voor productie.  
De H2-console is bereikbaar op `http://localhost:8080/h2-console` (JDBC URL = `jdbc:h2:mem:flightmaster`).

---

## Tests uitvoeren
Alle tests draaien via Maven:
```bash
./mvnw test
```

- **Unit-tests:** 100 % line-coverage op UserService en PassengerService (met Mockito).
- **Integratietests:** uitvoeren via `@SpringBootTest` en `MockMvc`.  
  Na het draaien verschijnt de test-samenvatting in de terminal.

---

## Autorisatieniveaus

| Gebruikersrol | Toegang                      |
|---------------|------------------------------|
| ADMIN         | Alle endpoints               |
| USER          | Passenger & Flight endpoints |
| PILOT         | PDF generatie endpoint       |
| GUEST         | Register endpoint            |


Autorisatie is geregeld via **JWT-tokens**:
1. Registreer of log in via `/auth/register` of `/auth/login`.
2. Kopieer de ontvangen `token`.
3. Voeg deze toe aan de header bij elk verzoek:
   ```
   Authorization: Bearer <token>
   ```

---
## Default Gebruikers
Bij het opstarten van de applicatie worden er automatisch default gebruikers toegevoegd om de applicatie te kunnen gebruiken. Waaronder ook de default ADMIN:

| Gebruiker | Wachtwoord | Rol  | Toegang |
|------------|-------------|------|----------|
| `admin@flightmaster.nl` | `Geheim123!` | ADMIN | Alle endpoints |

**Let op:** Verwijder na eerste startup de Admin insert regels uit data.sql of pas het wachtwoord aan.
Voeg eerst een extra Admin aan Flight Master toe met eigen credentials. Delete daarna de Default Admin en het data.sql bestand uit de repository wanneer je geen gebruik van de default data wilt gebruiken.

---

## Stappenplan na eerste login
1. Log in met de default Admin op endpoint: **/auth/login** 
   1. Gebruik in de body:
   ```bash
   {
     "email": "admin@flightmaster.nl"
     "password": "Geheim123!"
   }
   ```
   2. Kopieer de token uit de respons van bovenstaand request.

2. Voeg een eigen Admin account toe.
   1. Endpoint: **POST /users**
   2. Headers: 
   ```bash
   {
     Authorization: Bearer <jouw-token>
     Content-Type: application/json
   }
   ```
   3. Body
   ```bash
   {
     "email": "jouw@email.nl"
     "password": "Gebruik een sterk wachtwoord"
     "role": "ADMIN"
   }
   ```
3. Verwijder de default Admin
   1. Zoek het ID van de default Admin via **GET /users**.
   2. Verwijder het met de **DELETE /users/{id}** endpoint.
4. Verwijder of pas **data.sql** aan.
   1. Verwijder de regel met de default Admin **INSERT STATEMENT**

**Alleen het zelf aangemaakte Admin beheeraccount blijft actief en bij een komende applicatie startup, voegt de applicatie geen extra admin meer toe**

---

## Overige commando’s
| Doel | Commando |
|------|-----------|
| Project schoonmaken | `./mvnw clean` |


---

## Bronnen
- NOVI Hogeschool EdHub Leerlijn
- Documentatie “Eindopdracht Backend V4.1”  
- Maven.apache.org "Tools Wrapper"
- Markdownguide.org
