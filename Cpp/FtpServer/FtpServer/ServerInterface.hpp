#pragma once
#ifndef __SERVERINTERFACE_HPP__
#define __SERVERINTERFACE_HPP__

#include <iostream>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string>
#include <vector>

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

#endif // !__SERVERINTERFACE_HPP__
