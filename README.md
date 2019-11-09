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
* [Rajapinta](https://github.com/Hintahaukka/backend/blob/master/documentation/rajapinta.xlsx)

## Branch -käytännöt

Uusia ominaisuuksia varten tehdään aina oma branch. Kun ominaisuus on valmis, se mergetään dev-branchiin ja varmistetaan, että kaikki toimii. Lopuksi dev-branch mergetään masteriin.

## Asennusohjeet

Tässä osiossa esitellään vaadittavat toimenpiteet backend-palvelimen käynnistämiseksi paikallisesti, backend-palvelimen testaamista varten. Oletuksena on, että backend-repositorio on kloonattu omalle koneelle, ja että se on ajantasalla.

### Vain ensimmäisellä palvelimen käynnistyskerralla tehtävät toimenpiteet

1. Asenna PostgreSQL omalle koneelle:  
Käytä oletusasetuksia. Kun postgres nimiselle käyttäjälle pyydetään antamaan salasana, anna salasanaksi:  
salasana123

2. Lisää PATH-ympäristömuuttujaan hakemistopolku, joka osoittaa PostgreSQL:n bin-kansioon.  
Riippuen asennuspaikasta, Windowsissa lisättävä ympäristömuuttuja saattaa olla esimerkiksi muotoa:  
C:\Program Files\PostgreSQL\11\bin

3. Lisää uusi ympäristömuuttuja nimellä DATABASE_URL ja arvolla:  
postgres://postgres:salasana123@localhost:5432/postgres  
Tämä lisätään, jotta backend-palvelin voi toimia paikallisesti samoin kuin se toimii Herokulla.

### Jokaisella palvelimen käynnistyskerralla tehtävät toimenpiteet

4. Käynnistä PostgreSQL-palvelin komentorivillä.  
Jos kyseessä Windows, syötä PowerShell:iin komento:  
postgres -D 'C:\Program Files\PostgreSQL\11\data'

5. Avaa toinen komentorivi-ikkuna ja vaihda työskentelyhakemistoksi backend-repositorion juurihakemisto.

Tässä välissä voit suorittaa backend-palvelimen automaattiset testit.  
Jos kyseessä Windows, syötä PowerShell:iin komento:  
gradle test

6. Käynnistä backend-palvelin.  
Jos kyseessä Windows, syötä PowerShell:iin komento:  
gradle run

Kun backend-palvelin on käynnistetty, voit testata palvelimen rajapintaa tämän repositorion kansiosta "tools" löytyvällä HTML-lomakkeella "BackendRajapinnanTestaus.html".
