#pragma once
#ifndef __SERVERCLIENT_HPP__
#define __SERVERCLIENT_HPP__
#include "ServerInterface.hpp"
#include "ServerFuncInterface.hpp"

#include <experimental/filesystem>

namespace fs = std::experimental::filesystem;

class ServerClient :
    public ServerInterface, public ServerFuncInterface
{
private:
    struct sockaddr* addr;
    bool alive = false;
    int socket_fd;
    int ID = 1;
    std::string mode = "";
    fs::path dir = fs::path("/home");
    static socklen_t socklen;
public:
    ServerClient();
    ServerClient(struct sockaddr* addr, int ID);
    ~ServerClient();
    //bool CreateSocket(int sockfd, struct sockaddr* addr);
    bool CreateSocket();
    bool isAlive();
    bool Listen();
    int Accept();
    bool ServerMain();

    // Implementation of SererFuncInterface
    void list(std::vector<std::string>);
    void cd(std::vector<std::string>);
    void get(std::vector<std::string>);
    void put(std::vector<std::string>);
    void pwd();
    void port();
    void pasv();
    static std::vector<std::string> StringParser(const std::string str);

protected:
    bool Bind();
    bool Exit();
    int recvFromCli();
    int sendData(std::string str);

};

#endif // !__SERVERCLIENT_HPP__
