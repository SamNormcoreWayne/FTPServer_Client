#pragma once
#ifndef _SERVERCLIENT_HPP_
#define _SERVERCLIENT_HPP_
#include "ServerInterface.hpp"


class ServerClient :
    public ServerInterface
{
private:
    struct sockaddr* addr;
    bool alive = false;
    int socket_fd;
    int ID = 1;
    static socklen_t socklen;
public:
    ServerClient();
    ServerClient(struct sockaddr* addr, int ID);
    ~ServerClient();
    bool isAlive();
    //bool CreateSocket(int sockfd, struct sockaddr* addr);
    bool CreateSocket();
    void threeTimesHandShaking();
    bool ServerMain();
    bool Bind();
    bool Listen();
    int Accept();
    bool Exit();

    int recvFromCli();
    int sendData(std::string str);
};

#endif // !_SERVERCLIENT_HPP_