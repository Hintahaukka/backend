# Paikallinen kehitys- ja testausohje

Tässä osiossa esitellään vaadittavat toimenpiteet backend-palvelimen käynnistämiseksi paikallisesti, backend-palvelimen kehittämistä ja testaamista varten. Oletuksena on, että backend-repositorio on kloonattu omalle koneelle, ja että se on ajantasalla.

## Vain ensimmäisellä palvelimen käynnistyskerralla tehtävät toimenpiteet

1. Asenna PostgreSQL omalle koneelle:  
Käytä oletusasetuksia. Kun postgres nimiselle käyttäjälle pyydetään antamaan salasana, anna salasanaksi:  
salasana123

2. Lisää PATH-ympäristömuuttujaan hakemistopolku, joka osoittaa PostgreSQL:n bin-kansioon.  
Riippuen asennuspaikasta, Windowsissa lisättävä ympäristömuuttuja saattaa olla esimerkiksi muotoa:  
C:\Program Files\PostgreSQL\11\bin

3. Lisää uusi ympäristömuuttuja nimellä DATABASE_URL ja arvolla:  
postgres://postgres:salasana123@localhost:5432/postgres  
Tämä lisätään, jotta backend-palvelin voi toimia paikallisesti samoin kuin se toimii Herokulla.

## Jokaisella palvelimen käynnistyskerralla tehtävät toimenpiteet

4. Käynnistä PostgreSQL-palvelin komentorivillä.  
Jos kyseessä Windows, syötä PowerShell:iin komento:  
postgres -D 'C:\Program Files\PostgreSQL\11\data'

5. Avaa toinen komentorivi-ikkuna ja vaihda työskentelyhakemistoksi backend-repositorion juurihakemisto.

Tässä välissä voit tehdä palvelimen kehitystyötä.

Kehitystyön jälkeen on hyvä hetki suorittaa backend-palvelimen automaattiset testit.  
Jos kyseessä Windows, syötä PowerShell:iin komento:  
./gradlew.bat test
Jos kyseessä Linux, syötä komentoriviin komento:  
./gradlew test
Testiraportti löytyy repositorion paikallisesta versiosta polusta (gitignored):
build/reports/tests/test/index.html
Testien koodikattavuusraportti löytyy repositorion paikallisesta versiosta polusta (gitignored):
build/jacocoHtml/index.html

6. Käynnistä backend-palvelin.  
Jos kyseessä Windows, syötä PowerShell:iin komento:  
./gradlew.bat run
Jos kyseessä Linux, syötä komentoriviin komento:  
./gradlew run

Kun backend-palvelin on käynnistetty, voit testata palvelimen rajapintaa tämän repositorion kansiosta "tools" löytyvällä HTML-lomakkeella "BackendRajapinnanTestaus.html".
