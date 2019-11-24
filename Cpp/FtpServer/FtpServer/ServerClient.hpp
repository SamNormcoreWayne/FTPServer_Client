#pragma once
#ifndef __SERVERCLIENT_HPP__
#define __SERVERCLIENT_HPP__ 1
#include "ServerInterface.hpp"
#include "ServerFuncInterface.hpp"

#include <fstream>

#ifdef WINDOWS
#   include <direct.h>
#   define GetCurrentDir _getcwd
#else
#   include <unistd.h>
#   include <dirent.h>
#   define GetCurrentDir _getcwd
#endif

#ifdef __has_include
#   if __has_include(<experimental/filesystem>)
#       include <experimental/filesystem>
#       define have_filesystem 1
        namespace fs = std::experimental::filesystem;
#   elif __has_include(<filesystem>)
#       include <filesystem>
#       define have_filesystem 1
        namespace fs = std::filesystem;
#   else
#       define have_filesystem 0
#   endif
#endif

class ServerClient :
    public ServerInterface, public ServerFuncInterface
{
private:
    struct sockaddr* addr;
    bool alive = false;
    int socket_fd;
    int ID = 1;
    std::string mode = "";
#if have_filesystem == 1
    fs::path dir = fs::path("/home");
#else
    char dir[FILENAME_MAX];
    if (!GetCurrentDir(dir, sizeof(FILENAME_MAX)))
    {
        this->Exit();
        exit(-1);
    }
#endif
    static socklen_t socklen;
public:
    static int MainThreadsCount;
    static int TransThreadCount;

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
    void operator() ();
};

#endif // !__SERVERCLIENT_HPP__
