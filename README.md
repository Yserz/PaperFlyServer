PaperFlyServer
==============


1. Netbeans:
    - Clean and Build: Paper-Fly_Server parent 

2. CREATE DATABASE paperfly DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

3. Datenbank Benutzer anlegen:
    - name: paperflyapp@localhost
    - pw:   admin
    - alle Rechte auswählen

3. Start Paperfly-Server auf Glassfish 4.0

4. Teste http://localhost:4848/

5. Security Real einrichten Glassfish
    - http://localhost:4848
    - zu finden unter Konfiguration/server-config/Sicherheit/Realms

6. Teste login
  - localhost:8080/PaperFlyServer-web/secure
  - standarduser: mail@mail.de
  - pw: 123456
