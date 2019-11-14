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

bool ServerClient::ServerMain()
{
    /**
     * Do something here.
     */
    enum command {help, ls, cd, port, pasv, get, put, bye};
    std::string in_commd;
    std::vector<std::string> in;
    while(true)
    {
        std::cin >> in_commd;
        if (in.size() == 0)
        {
            continue;
        }
        if (in.size() >= 3)
        {
            std::cout << "Illegal input. Try \"help\"." << std::endl;
            std::cout.flush();
            continue;
        }
        if (in[0] == "help")
        {
            /**
             * some helps here
             */
            continue;
        }
        if (in[0] == "ls")
        {
            this->list(in);
            continue;
        }
        if (in[0] == "cd")
        {
            this->cd(in);
            continue;
        }
        if (in[0] == "port")
        {
            this->port();
            continue;
        }
        if (in[0] == "pasv")
        {
            this->pasv();
            continue;
        }
        if (in[0] == "get")
        {
            this->get(in);
            /**
             * get could be a async method
             */
            continue;
        }
        if (in[0] == "put")
        {
            if(this->mode == "")
            {
                std::cout << "Setup mode first. Try \"pasv\" or \"port\"" << std::endl;
                continue;
            }
            this->put(in);
            /**
             * put could be a async method
             */
            continue;
        }
        if (in[0] == "bye")
        {
            this->Exit();
        }
        std::cout << "Bad input. Try \"help\"." << std::endl;
    }
}

std::vector<std::string> ServerClient::StringParser(const std::string str)
{
    /**
     * Test required.
     */
    int j = 0;
    std::vector<std::string> tmp;
    for (int i = 0; i < str.size(); ++i)
    {
        if (str.size() == 1)
        {
            tmp.push_back(str);
            break;
        }
        if (str[i] == ' ')
        {
            if (i - j == 0)
            {
                j = i + 1;
                continue;
            }
            tmp.push_back(str.substr(j, i - j));
        }
        j = i + 1;
    }
    return tmp;
}