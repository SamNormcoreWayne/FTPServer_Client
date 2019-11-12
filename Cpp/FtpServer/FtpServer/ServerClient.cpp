#include "ServerClient.hpp"

#define LOCAL_HOST "127.0.0.1"

ServerClient::ServerClient()
{
    /**
     * Defualt constructor
     * Create server with localhost
     * PORT: 8000
     */
    struct sockaddr_in def;
    def.sin_family = AF_INET;
    def.sin_addr.s_addr = inet_addr(LOCAL_HOST);
    def.sin_port = htons(8000);
    this->addr = (struct sockaddr *)&def;
}

ServerClient::ServerClient(struct sockaddr* addr, int ID)
{
    this->addr = addr;
    this->ID = ID;
    std::cout << "Initializing..." << std::endl;
    this->CreateSocket();
}


ServerClient::~ServerClient()
{
}

bool ServerClient::CreateSocket()
{
    if ((this->socket_fd = socket(PF_INET, SOCK_STREAM, 0)) < 0)
    {
        this->Exit();
    }
    else
    {
        this->alive = true;
        return true;
    }
}

bool ServerClient::isAlive()
{
    return this->alive;
}
