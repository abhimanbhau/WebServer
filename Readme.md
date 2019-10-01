Abhiman Kolte
00001414541
Programming Assignment 1
02/01/2019

Simple web server written in java which is multi-threaded.
Uses Java SocketServer API to serve requests originated from the client.
Responds with correct HTTP responses. Supports plain text, html, images, css files.

Readme.txt script.pdf [Makefile]


Instructions to run the server
1. Assuming you're in the root which is Makefile directory, run following commands
2. javac xyz/abhiman/Main.java
3. java xyz.abhiman.Main <root_dir> <port>
4. Example is java xyz.abhiman.Main /Users/akolte/distr 1234
5. Important note: Pleases use the absolute address of the document root directory, relative address won't work and do not put the trailing slash.
e.g. if you want to host your home directory use
java xyz.abhiman.Main /home/John 1234


Uses standard Java 8 APIs, no external library is used. Each request is processed onto a separate thread.
