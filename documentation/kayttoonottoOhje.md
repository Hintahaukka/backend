# Käyttöönotto-ohje

Alkuoletuksena on, että on admin-oikeudet GitHub-repositorioon jossa Hintahaukan Backend-koodi sijaitsee.

## Travis CI konfigurointi

1. Rekisteröidy Travis CI -palveluun ja kirjaudu sisään Travis CI -palveluun (Nämä molemmat onnistuvat GitHub-tunnuksilla).
2. Mene Travis CI -käyttäjäsi asetuksiin, ja aktivoi sieltä Travis CI -palvelu Hintahaukka Backend -repositoriollesi.
3. Mene Hintahaukka Backend -repositoriollesi ja asenna repositoriolle Travis CI:n GitHub App.<br/>
Tämä varmistaa että repositorion Travis CI integraatio varmasti toimii (Hintahaukkaa tehtäessä integraatio toimi ensin hyvin ilman Appia, mutta lopetti asteittain toimintansa ilman Appia).

## Heroku konfigurointi

4. Rekisteröidy Heroku-palveluun ja kirjaudu sisään Heroku-palveluun.
5. Mene Heroku-käyttäjäsi Dashboardille ja klikkaa "New" ja "Create new app".<br/>
Avautuvassa näkymässä anna App:lle nimi, valitse palvelimen maantieteellinen sijainti ja klikkaa "Create app".<br/>
Seuraavaksi avautuvassa näkymässä valitse "Deployment method GitHub", ja tämän jälkeen alle avautuvassa "Connect to GitHub" -osiossa integroi Heroku GitHub-tunnuksesi kanssa ja sitten etsi Hintahaukka Backend -repositoriosi listalta ja valitse se.<br/>
Tämän jälkeen laita automaattiset deploymentit päälle master-branchista ja laita myös rasti ruutuun "Wait for CI to pass before deploy".

## Heroku PostgreSQL konfigurointi

6. Mene Herokun Hintahaukka Backend App:isi Overview-sivulle ja klikkaa "Configure Add-ons".<br/>
Avautuvassa näkymässä etsi hakukentän avulla "Heroku Postgres" ja liitä kyseinen add-on Herokun Hintahaukka Backend App:iisi.<br/>
Odota n. 10 min, sillä tämän add-on:in aktivoituminen saattaa kestää hetken.

## Viimeistely

7. Tee jokin pieni muutos master-branchiin ja pushaa muutos GitHubiin. Tämän pitäisi aktivoida Travis CI:n tarkastamaan koodin ja Travis CI:n hyväksymisen jälkeen Herokun hakemaan koodin ja alkamaan suorittamaan koodia. Odota tämän jälkeen niin kauan kunnes Herokun Hintahaukka Backend App:isi Overview-sivu näyttää että koodi on deployed.
8. Luo tietokantaan vaaditut tietokantataulut käymällä nettiosoitteissa:<br/>
https://HEROKU-APPISI-NIMI.herokuapp.com/reset/public <br/>
https://HEROKU-APPISI-NIMI.herokuapp.com/reset/test <br/>
9. Jotta Hintahaukka Android -sovelluksesi osaa käyttää Hintahaukka Backend -palvelintasi,<br/>
muuta Hintahaukka Android -sovelluksesi koodista kaikki hintahaukka.herokuapp.com kohdat muotoon HEROKU-APPISI-NIMI.herokuapp.com


## Loppusanat

Nyt Hintahaukka Backend -repositorioasi vastaava palvelinohjelma pitäisi olla toiminnassa.<br/>
Nyt aina kun teet muutoksia repositorion master-branchiin niin muutosten pitäisi automaattisesti päivittyä Herokulla suoritettavaan palvelinohjelmaan kunhan Travis onnistuu buildaamaan ohjelman ja kunhan testien suoritus onnistuu Travisissa.

Kaikista palvelimen nykyisistä toiminnoista on 2 versiota, versio joka käyttää tuotantotietokantaa sekä versio joka käyttää testitietokantaa (Toteutettu PostgreSQL:n Schema-toiminnallisuudella).<br/>
Voit omassa kehityksessä jatkaa samaa käytäntöä, ja esim. täten voit tuoda uudet ominaisuudet ensin testikäyttöön jättämällä tuotanto-routin julkaisematta ennen kuin ominaisuus on käytännössä testattu testi-routin avulla.<br/>
Nämä versiot toteutettiin, jotta testitietokantaversiota voidaan käyttää staging-palvelimen korvikkeena. Erillistä staging-palvelinta ei käytetty Herokun ilmaisversion rajoitteiden takia.


### Muutokset tietokannan rakenteeseen

Tällä hetkellä palvelinohjelmisto osaa ainoastaan tyhjentää koko tietokannan ja sitten asentaa kaikki vaaditut tietokantataulut tietokantaan.<br/>
Jos tietokannan rakenteeseen on tarve tehdä hienovaraisempia muutoksia, niin nämä voi tehdä tietokantojen käsittelyohjelmalla, kuten esim. pgAdmin:lla.<br/>
Herokun PostgreSQL -tietokannan osoitteen, käyttäjätunnuksen ja salasana löydät Heroku Dashboardin Hintahaukka Backend Appisi välilehdeltä "Settings".<br/>
Paina osion "Config Vars" nappia "Reveal Config Vars". "DATABASE_URL" muuttujasta löydät kaikki nämä tiedot.<br/>
Muuttuja on muodossa: <br/>
postgres://KÄYTTÄJÄTUNNUS:SALASANA@TIETOKANTAPALVELIMEN_HOSTNAME:PORTTI/TIETOKANNAN_NIMI <br/>
Herokun mukaan tämä muuttuja voi koska tahansa muuttua, eli tietokantasi sijainti ja tiedot voi koska tahansa muuttua, mutta käytännössä muuttuja pysyy pitkiä aikoja samana, joten voit jättää muuttujan tiedot tietokannan käyttöohjelmasi muistiin ja ne luultavasti ovat valideja myös seuraavalla kerralla kun tulee tarve päästä käsiksi tietokantaan.

### Koodin palvelukohtaiset osat

Kaikkien näiden palveluiden, Travis CI:n, Herokun ja Heroku PosgreSQL:n, pitäisi toimia Hintahaukka Backend -repositorion kanssa ylläolevien ohjeiden mukaan, ilman tarvetta tehdä muutoksia tai lisäyksiä Hintahaukka Backend -repositorion sisältämään koodiin. Hintahaukka Backend -repositorioon on konfiguroitu valmiiksi kaikkien näiden palveluiden vaatimat asetukset ja koodit.

Ohessa yhteenveto tiedostoista ja niiden kohdista jotka ovat spesifejä näille palveluille:
* .travis.yml (Travisia varten)
* Procfile (Herokua varten)
* src/main/java/hintahaukka/App.java, getHerokuAssignedPort()-metodi (Herokua varten)
* src/main/java/hintahaukka/database/Database.java, getConnection()-metodi (Heroku PostgreSQL varten)
