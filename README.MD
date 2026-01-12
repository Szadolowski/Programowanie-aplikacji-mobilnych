### Apliukacja licząca kroki

## Śledzenie kroków

## Przedstawienie trasy na mapie

## Cel kroków dziennych

## SQLite

# 12.01.2026

-Dodanie MapBox do zakładki MAPA
-Dodanie przycisku wyśrodkowania

-Przywrócenie stosowanie akcelometru do aplikacji

-Zaimplementowanie bazy danych SQLite i Room do zarządzania komunikacją z bazą
(

1.  DailySummary: Tabela poziom ogólny.
    ◦
    date: Klucz główny w formacie yyyy-MM-dd. Dzięki temu łatwo sumować dane z konkretnego dnia.
    ◦
    totalSteps: Sumaryczna liczba kroków.
2.  Workout: Tabela poziom szczegółowy (Treningi).
    ◦
    parentDate: Klucz obcy (Foreign Key) wskazujący na DailySummary z opcją CASCADE (usunięcie dnia usuwa treningi).
    ◦
    startTime / endTime: Dokładne znaczniki czasu.
    ◦
    routePointsJson: Dane trasy.
    )
