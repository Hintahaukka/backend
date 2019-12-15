# Hintahaukka Backend

## CI

| Branch | Tila |
| --- | --- |
| master | [![Build Status](https://travis-ci.org/Hintahaukka/backend.svg?branch=master)](https://travis-ci.org/Hintahaukka/backend)  |
| dev | [![Build Status](https://travis-ci.org/Hintahaukka/backend.svg?branch=dev)](https://travis-ci.org/Hintahaukka/backend) |

### Test code coverage:
| Branch | Tila |
| --- | --- |
| master | [![codecov](https://codecov.io/gh/Hintahaukka/backend/branch/master/graph/badge.svg)](https://codecov.io/gh/Hintahaukka/backend) |
| dev | [![codecov](https://codecov.io/gh/Hintahaukka/backend/branch/dev/graph/badge.svg)](https://codecov.io/gh/Hintahaukka/backend) |

## Repositoriot

* [Android-sovellus (Projektin päärepositorio)](https://github.com/Hintahaukka/application)
* Backend (Tämä repositorio)

## Backendin dokumentaatio
* [Tietosisältökaavio](https://github.com/Hintahaukka/backend/blob/master/documentation/tietosisaltokaavio.png)
* [Tietokantataulujen luontilauseet](https://github.com/Hintahaukka/backend/blob/master/documentation/tietokantataulujenLuontilauseet.txt)
* [Palvelimen rajapinta](https://docs.google.com/spreadsheets/d/1Mazq4EFbfbMsLPeCpOckbu11LNR1Ki2RiNf460z-rpU/edit#gid=1349149505)
* [Paikallinen kehitys- ja testausohje](https://github.com/Hintahaukka/backend/blob/master/documentation/paikallinenKehitysJaTestaus.md)
* [Käyttöönotto-ohje](https://github.com/Hintahaukka/backend/blob/master/documentation/kayttoonottoOhje.md)

## Hintahaukka Backend -projektissa käytetyt teknologiat, frameworkit ja palvelut
| Aihe | Toteutus |
| --- | --- |
| Versionhallinta | Git |
| Versionhallinta-palvelin | GitHub |
| Build-automation system | Gradle |
| Web framework | Spark Framework |
| Ohjelmointikieli | Java |
| Tietokanta | PostgreSQL |
| CI-palvelu | Travis CI |
| PaaS-palvelu | Heroku |

## Branch -käytännöt

Uusia ominaisuuksia varten tehdään aina oma branch. Kun ominaisuus on valmis, se mergetään dev-branchiin ja varmistetaan, että kaikki toimii. Lopuksi dev-branch mergetään masteriin.
