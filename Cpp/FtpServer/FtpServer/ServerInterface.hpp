#pragma once
#include <iostream>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>

class ServerInterface
{
public:
    ServerInterface() {}
    virtual ~ServerInterface() = 0;
    virtual bool CreateSocket() = 0;
    virtual bool isAlive() = 0;
    virtual bool Listen() = 0;
    virtual int Accept() = 0;
    virtual bool ServerMain() = 0;
};