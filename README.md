# 🛍️ Unilire – E-commerce Retrò con Tecnologia Moderna

**Unilire** è una piattaforma e-commerce full-stack che fonde il fascino nostalgico delle lire italiane con l’efficienza e la flessibilità delle tecnologie moderne. Ispirato al design degli anni '80, il progetto offre un’esperienza d’acquisto curata nei dettagli, sicura e completamente personalizzabile, sia per gli utenti finali che per gli amministratori.

---

## 🎯 Funzionalità Principali

- 🔍 **Ricerca semantica avanzata** con fuzzy matching per nomi, descrizioni e categorie
- 🎁 **Motore di raccomandazione** personalizzato (co-acquisti, prodotti correlati, cronologia)
- 🛒 **Carrello intelligente** con aggiornamento stock in tempo reale e gestione sconti
- 🧾 **Fattura PDF automatica** con layout retrò, logo, riepilogo dettagliato e data
- 🚚 **Checkout completo** con calcolo dinamico di spedizione, sconti e coupon
- 💬 **Sistema reclami e messaggistica** bidirezionale tra utente e staff
- 📜 **Wishlist persistente** e cronologia ordini filtrabile
- 🎛️ **Pannello di amministrazione** per la gestione di prodotti, utenti, ordini, coupon e statistiche
- 🌙 **Modalità scura integrata**, attivabile da ogni pagina
- 🔐 **Autenticazione sicura** con JWT, ruoli, protezione delle rotte e token refresh

---

## 🧱 Architettura del Progetto

---text
📦 Unilire
├── 📡 Backend: Spring Boot 3.4 (REST API)
│   ├── Sicurezza avanzata con JWT e autorizzazioni per ruolo
│   ├── Servizi modulari: prodotti, carrello, ordini, reclami, checkout
│   ├── Lock ottimistico (JPA @Version) per la concorrenza sul carrello
│   ├── Generazione PDF, cache Caffeine e invio email
│   └── Firebase Cloud Storage per la gestione immagini
├── 🎨 Frontend: Angular 17 SPA
│   ├── Design retrò ispirato agli anni '80 con dark mode
│   ├── Componenti riutilizzabili e routing sicuro (AuthGuard)
│   ├── Admin panel separato con gestione CRUD completa
│   └── Interfaccia responsive ottimizzata per dispositivi mobili
└── 🛢️ Database: PostgreSQL 


---

## 🛠️ Stack Tecnologico

| Livello      | Tecnologie                                       |
|--------------|--------------------------------------------------|
| **Frontend** | Angular 17, Angular Material, SCSS, Chart.js     |
| **Backend**  | Spring Boot 3.4, Spring Security, JPA, Lombok    |
| **Database** | PostgreSQL, Flyway                               |
| **Storage**  | Firebase Cloud Storage (per immagini prodotti)   |
| **Utility**  | PDFBox, Caffeine Cache, JWT, Apache Commons      |
| **Testing**  | JUnit 5, Mockito, Testcontainers                  |

---



## 🚀 Avvio Locale

### 🔧 Backend (Spring Boot)

--- 
bash
./mvnw spring-boot:run






---

## 👨‍🎓 Autore e Università

**Paolo Pangallo**  
Corso di Laurea in Ingegneria Informatica  
**Università della Calabria**  
📧 paolo.pangallo@studenti.unical.it  
🔗 [github.com/paolopangallo](https://github.com/paolopangallo)

---

> “Unilire è più di un e-commerce: è un ponte tra memoria collettiva e innovazione digitale.”
