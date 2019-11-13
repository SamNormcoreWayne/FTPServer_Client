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
    virtual int recvFromCli() = 0;
    virtual int sendData(string str);
};