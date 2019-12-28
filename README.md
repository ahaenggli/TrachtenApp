# TrachtenApp
Android-App für die Homepage der Trachtengruppe Merenschwand  
Es ist eigentlich "nur" eine WebView mit RSS-Feed-Überwachung. Es wird die Mobile-Version der Homepage angezeigt.  
Der BroadcastReceiver hält den RssService am Laufen. Im RssService wird der RSS-Feed der Homepage überwacht. Wenn es einen neuen Eintrag gibt, wird man entsprechend via Push darüber informiert.
