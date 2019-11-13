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
    int recvFromCli();
    int sendData(std::string str);

    bool Bind();
    bool Listen();
    int Accept();
    bool Exit();

};

#endif // !_SERVERCLIENT_HPP_