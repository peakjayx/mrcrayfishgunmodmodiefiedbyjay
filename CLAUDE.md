\# Coding Style Guidelines



\## 1. Clean \& Concise Code

\- Schreib lesbaren, sauberen Code.

\- Verhindere "Code-Wurst": Brich lange Zeilen konsequent um.

\- Minimiere die Zeichen pro Zeile (Horizontal Scrolling ist Tabu).



\## 2. Minimalist Comments

\- Kein Laber-Rababer oder AI-Spam.

\- Maximal 3 Wörter pro Funktions-Kommentar.

\- Nur das Nötigste. Beispiel: `// Ticketsystem Logik`



\## 3. Tone \& Grammar

\- Im Code: Korrekte Rechtschreibung und Syntax.

\- In Kommentaren: Kannst schlampen. Kleinschreibung und Slang sind fine.



\## 4. Minimalist Error Handling

\- Kein Over-Engineering bei Fehlern oder Edge-Cases. 

\- Fang nur das ab, was wirklich krachen kann.

\- Fehlertexte kurz halten, kein Roman.



\## 5. Output \& Deployment

\- Nach dem Code nur kurze Action-Steps.

\- Beispiel: "npm install, npm start, dann localhost:3000 checken."

\- Keine langen Erklärungen, warum du was gemacht hast.



\## 6. Structure \& Token Efficiency

\- Arbeite modular (wie in Java): Viele Packages/Unterdateien statt Monolithen.

\- Halte Files klein, um Token-Limit bei Re-Reads zu sparen.

\- Update nach JEDER Code-Änderung die `structure.md`.

\- `structure.md` Listet alle Files + 1-Satz-Info zum Inhalt.

\- WICHTIG: Lies IMMER zuerst die `structure.md`, um den aktuellen Stand zu kennen.

