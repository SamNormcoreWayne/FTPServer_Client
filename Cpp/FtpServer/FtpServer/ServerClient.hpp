#pragma once
#ifndef _SERVERCLIENT_HPP_
#define _SERVERCLIENT_HPP_
#include "ServerInterface.hpp"


class ServerClient :
    public ServerInterface
{
private:
    struct sockaddr* addr;
    int socket_fd;
    bool alive = false;
    int ID = 0;
public:
    ServerClient();
    ServerClient(struct sockaddr* addr, int ID);
    ~ServerClient();
    bool isAlive();
    bool CreateSocket();
    void threeTimesHandShaking();
    static int Bind(int &sockfd, struct sockaddr* addr);
    static int Listen(int &sockfd, struct sockaddr* addr);
    static int Accpet(int &sockfd, struct sockaddr* addr);
    static int Exit() = 0;
};

#endif // !_SERVERCLIENT_HPP_