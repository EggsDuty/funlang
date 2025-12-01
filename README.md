# funlang-2025

Siūlau IntelliJ IDEA naudoti jei turit, bet galima ir be jo.

## Reikia
- Java 21
- Maven

Jei naudojat IntelliJ IDEA tai turėtų šituos kaip ir automatiškai setupint arba apačioje bus nuotraukos kaip juos setupint atskirai.

Įjungus IntellIJ IDEA siūlys antlr4 pluginą, tai siūlau parsisiųsti, nes paspalvins tada tą "g4" failą. VSCode taip pat turi "ANTLR4 grammar syntax support" pluginą.

## Paleidimas
Projektą buildinti reikia įvesti
```
mvn clean package
```
arba IntelliJ nueiti į Maven, paspausti "Execute Maven Goal" ir ten įvesti `mvn clean package`
<img width="1709" height="493" alt="image" src="https://github.com/user-attachments/assets/bf3dda0b-ac84-4d1f-988d-acec9361a56b" />

Tuomet, kad paleisti vieną iš "sample" failų reikia įvesti tokią komandą
```
java -cp .\target\funlang-2025-1.0.jar edu.ktu.funlang.app.Main .\samples\variable.funlang
```
Aišku galas čia yra failas iš "samples" aplanko.

## Setup
Čia tiesiog AI nuotraukos, kurias siuntė Rosvaldas. Pačiam nereikėjo, bet gal padės, jei reikės

Maven:

<img width="708" height="1154" alt="image" src="https://github.com/user-attachments/assets/757dc81c-62b4-420f-af4c-0f663b0ef1bd" />

Java 21:

<img width="758" height="1207" alt="image" src="https://github.com/user-attachments/assets/e3897074-8df6-4155-bda6-1f279534357b" />

