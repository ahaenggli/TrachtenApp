# TrachtenApp
Android-App für die Homepage der Trachtengruppe Merenschwand  
Es ist eigentlich "nur" eine WebView mit RSS-Feed-Überwachung. Es wird die Mobile-Version der Homepage angezeigt.  
Der BroadcastReceiver hält den RssService am Laufen. Im RssService wird der RSS-Feed der Homepage überwacht. Wenn es einen neuen Eintrag gibt, wird man entsprechend via Push darüber informiert.

## Screenshots
![Startseite](tmp/Screenshot_1577622873.png?raw=true "Startseite")
![Benachrichtigungen](tmp/Screenshot_1577622879.png?raw=true "Benachrichtigungen")
![App-Icon](tmp/Screenshot_1577622899.png?raw=true "App-Icon")
![Push-Notification](tmp/Screenshot_1577622905.png?raw=true "Push-Notification")
![Push-Destination](tmp/Screenshot_1577622911.png?raw=true "Push-Destination")