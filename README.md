## This is a repo for FTP

### CPP

* main.cpp: Main function
* ServerClient: The class of server side client
* ServerFunc: the operations of FTP e.g. cd, ls, pasv, port, etc.

### Complile advises:

* Better to use compilers supporting `#include<filesystem>` or `#include<experimental/filesystem>`
* If not, the program would call system APIs.
    - Does not make sure codes can be run properly in Windows