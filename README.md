# Ευχαριστίες

Θέλω να ευχαριστήσω του διδάσκοντες για το πρόγραμμα. Στην αρχή, είχα πολύ μικρή εμπειρία με τον προγραμματισμό. Παρότι η εμπειρία μου ήταν μικρή,
το πρόγραμμα μου έδωσε τις γνώσεις που χρειαζόμουν ώστε να προχωρήσω γρήγορα στην επαγγελματική μου σταδιοδρομία.
Ήδη, από τον δεύτερο μήνα μετά την έναρξη των σπουδών, κατάφερα να βρω δουλειά ως full stack developer σε ελληνική εταιρεία.
Είμαι χαρούμενος που ολοκλήρωσα αυτό το πρόγραμμα και θεωρώ ότι με βοήθησε πολύ να βρω δουλειά και να ανταπεξέλθω ευκολότερα στις απαιτήσεις της.

## Σύντομη Περιγραφή

Πρόκειται για μια πολύ απλή web application διαχείρισης εργασιών. Αποτελείται από δύο στρώματα το backend και το frontend, τα οποία επικοινωνούν μέσω REST API.
Προτείνεται να γίνει είτε δημιουργία καινούργιου χρήστη μέσω της αντίστοιχης επιλογής στην αρχική σελίδα, ή να γίνει login με τα στοιχεία του default διαχειριστή και
στη συνέχεια να εξερευνηθούν οι αντίστοιχες σελίδες. Ο διαχειριστής, έχει πρόσβαση σε μία δύο ακόμα σελίδες την μία  μπορεί να δει και να επεξεργαστεί τους χρήστες και στην άλλη τα task τους.

## Deployment

Προτείνω την εγκατάσταση σε docker, καθώς την έχω κάνει πολύ απλή.
Απλά σε ένα linux περιβάλλον με docker και git τρέχεις τις εντολές (περιγράφονται παρακάτω) από το command line και η εφαρμογή είναι έτοιμη και λειτουργική χωρίς να χρειάζεται τίποτα άλλο.
Προσοχή! Η βάση δε χρειάζεται να έχει το σχήμα πριν ξεκινήσει καθώς έχω χρησιμοποιήσει flyway για migrations. Το schema δημιουργείται, αν δεν υπάρχει μέσω, με την έναρξη της εφαρμογής.

Πρώτα: πρέπει να τρέχει μια βάση MySQL 8.4 τοπικά
και να γίνει παραμετροποίηση για τα στοιχεία της βάσης στο AUEB_TODO_WEB_APP/docker-compose.yml
στις γραμμές 10-12

```
      SPRING_DATASOURCE_URL: "jdbc:mysql://host.docker.internal:3306/todo_mysql_tasos"
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "P@ssw0rd"
```

1) mkdir todoAppTasos
2) cd todoAppTasos
3) git init
4) git clone https://github.com/Zorgon212/AUEB_TODO_WEB_APP.git
5) cd AUEB_TODO_WEB_APP
6) git checkout v0.1 (Το branch που είναι πλήρως λειτουργικό, είναι το v0.1)

!!!! Παραμετροποίηση του docker-compose τοπικά

7) docker compose up --build -d
8) από browser, πηγαίνουμε στο URL http://localhost:8081/

Τα container που σηκώνονται έτσι, είναι το backend, frontend, nginx

Πρέπει να υπάρχει ήδη μία βάση MySQL και να έχει γίνει παραμετροποίηση του docker-compose

## Εναλλακτικό Deployment

Πρώτα, δημιουργούμε μία βάση MySQL και παραμετροποιούμε το backend ώστε να επικοινωνεί με αυτή

τραβάμε το project από το git, ανοίγουμε το frontend και το backend στα IDE της προτίμησής μας. Ανοίγουμε το terminal.
Για το backend: mvn package, και τρέχουμε το todoWebApplication.jave
Για το frontend: npm install, npm run dev
(δεν χρειάζεται ngrok για να τρέξει έτσι)

## Stacks

### Backend

Java 26
Spring 4

από spring χρησιμοποιώ:
- Spring MVC
- Spring data JPA
- Spring security (session cookie)
- Spring Flway integration
- Test starters
- DevTools

### Frontend

Sveltekit
Svelte 5

Γιατί τα συγκεκριμένα stack;
Τα stack επιλέχθηκαν για λόγους εξοικείωσης.

## Εργαλία / Τεχνολογίες

Κατά την ανάπτυξη της εφαρμογής χρησιμοποιήθηκαν τα εξής εργαλεία:

1) IntekiJ community edition για την ανάπτυξη του backend
2) VScode για την ανάπτυξη του frontend
3) Claude AI για την επιτάχυνση της διαδικασίας ανάπτυξης
4) spring initializr για τη δημιουργία του pom.xml
5) postman κατά τη δημιουργία του backend για test των κλήσεων

## Χρήση

Μπείτε στο url: http://localhost:8081/
Μπορεί όποιος θέλει να κάνει register μία φορά ανά email.

Οι σελίδες, φαίνονται στη δομή του frontend κάτω από τα routes

```
├───routes
│   ├───admin
│   │   ├───myinfo
│   │   ├───todos
│   │   │   └───[todoId]
│   │   └───users
│   │       └───[userId]
│   └───user
│       ├───myinfo
│       └───todos
│           └───[todoId]
```

Δημιουργείται ένα default profil διαχειριστή από το migration, αν δεν έχει δημιουργηθεί ήδη
Με credentials
- email: admin@todoApp.gr
- Password: P@ssw0rd

Η δημιουργία άλλου διαχειριστή μπορεί να γίνει μόνο μέσω διαχειριστή
και δημιουργώ ένα default user profil
Με credentials
- email: user@todoApp.gr
- Password: P@ssw0rd

## ΒΔ

Όπως ανέφερα και πριν η βάση, πρέπει απλά να υπάρχει και δημιουργείται το schema από το flyway,
αν υπάρχει ήδη, δε πειράζει αρκεί να είναι σωστό.

ER Diagram:

```
users (1) --< (many) tasks
   id--------------- user_id
```

tables:

A)

```
users
---------------------------
id                INT (PK)
full_name         VARCHAR(255)
email             VARCHAR(255)
password          VARCHAR(255)
status            BOOLEAN
user_type         VARCHAR(50)
```

B)

```
tasks
---------------------------
id                  INT (PK)
description         VARCHAR(255)
status              BOOLEAN
user_id             INT (FK -> users.id)
declared_time_id    DATETIME
completion_time_id  DATETIME
```

## TESTING

Backend tests:
ανοίγω το backend στο IntelliJ και κάνω `mvnw.cmd test`,
χρειάζεται να τρέχει η βάση δεδομένων για ένα από τα τεστ.

frontend tests:
Ανοίγω το frontend στο vscode και κάνω
`npm install`, `npx playwright install`, `npx playwright test`
Σε windows, πρέπει να εξαιρεθεί ο φάκελος από το windows defender γιατί δεν αφήνει το playwright να εγκατασταθεί σωστά.
Για να τρέξουν τα τεστ στο frontend, χρειάζεται το backend και η βάση δεδομένων να έχουν σηκωθεί.
