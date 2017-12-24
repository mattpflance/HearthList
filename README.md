# HearthList
An app for Blizzard's game HearthStone that displays all cards and will eventually allow users to create and save decks.

## Screenshots
![](/screenshots/main.png)    ![](/screenshots/details.png)

## Details
Google's TagManager is used so that I can control when new updates are pushed to clients. I update a variable in TagManager and the client will fetch this variable occaisionally to determine when it should re-query the API.

Card details are stored in a ContentProvider for offline access.

## Upcoming Features
Deck creation (TBA)
