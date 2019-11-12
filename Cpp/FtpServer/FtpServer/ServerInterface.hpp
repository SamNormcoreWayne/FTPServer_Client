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
    virtual int Bind() = 0;
    virtual int Listen() = 0;
    virtual int Accpet() = 0;
    virtual int Response() = 0;
    virtual int Exit() = 0;
};