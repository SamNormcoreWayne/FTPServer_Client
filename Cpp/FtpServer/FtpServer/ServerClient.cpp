#include "ServerClient.hpp"

constexpr auto LOCAL_HOST = "127.0.0.1";

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
    this->socklen = sizeof(struct sockaddr_in);
    this->CreateSocket();
}

ServerClient::ServerClient(struct sockaddr* addr, int ID)
{
    this->addr = addr;
    this->ID = ID;
    std::cout << "Initializing..." << std::endl;
    if (!this->CreateSocket())
        this->Exit();
}


ServerClient::~ServerClient()
{
}

//bool ServerClient::CreateSocket(int sockfd, struct sockaddr* addr)
//{
//    this->socket_fd = sockfd;
//    this->addr = addr;
//    return this->CreateSocket();
//}

bool ServerClient::CreateSocket()
{
    if ((this->socket_fd = socket(PF_INET, SOCK_STREAM, 0)) < 0)
    {
        this->Exit();
    }
    return this->Bind();
}

bool ServerClient::isAlive()
{
    return this->alive;
}

bool ServerClient::Bind()
{
    if (bind(this->socket_fd, this->addr, this->socklen) < 0)
    {
        perror("Bind error!");
        return false;
    }
    return true;
}
bool ServerClient::Listen()
{
    if (listen(this->socket_fd, 5) < 0)
    {
        perror("Listen error!");
        return false;
    }
    return true;
}

int ServerClient::Accept()
{
    int client_fd;
    if ((client_fd = accept(this->socket_fd, this->addr, &ServerClient::socklen)) < 0)
    {
        return -1;
    }
    return client_fd;
}
