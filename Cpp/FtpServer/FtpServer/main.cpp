#include <iostream>
#include <cstring>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "ServerInterface.hpp"
#include "ServerClient.hpp"

using namespace std;

bool legalIP(char*);
bool legalPort(char*);
bool legalID(char*);

int main(int argc, char *argv[]){
    return 0;
    /**
     * Possible args:
     * 1. --host
     * 2. --http
     * 3. --port
     * 4. --ID
     */
    enum parm_t {ip, port, ID};
    int server_sockfd;
    int clint_sockfd;

    /*
     * TODO: Implement legalIP(), legalPort(), legalIP();
     */
    struct sockaddr_in servAddr;
    servAddr.sin_family = AF_INET;
    servAddr.sin_addr.s_addr = INADDR_ANY;
    servAddr.sin_port = htons(8000);

    int serverID = 0;
    for (int i = 0; i < argc; ++i)
    {
        if (argv[i] == "--ip")
        {
            if (legalIP(argv[i + 1]))
                servAddr.sin_addr.s_addr = inet_addr(argv[i + 1]);
            else
            {
                cout << "Illegal IP address!" << endl;
                exit(-1);
            }
        }
        if (argv[i] == "--port")
        {
            if (legalPort(argv[i + 1]))
            {
                servAddr.sin_port = htons(atoi(argv[i + 1]));
            }
            else
            {
                cout << "Illegal Port number!" << endl;
                exit(-1);
            }
        }
        if (argv[i] == "--ID")
        {
            if (legalID(argv[i + 1]))
            {
                serverID = atoi(argv[i + 1]);
            }
            else
            {
                cout << "Illegal ID number! ID number shall be between 0 to 9999" << endl;
                exit(-1);
            }
        }
    }
    /**
     * Concurrency required in future.
     * Multi-thread -> Thread pool -> Multiplexing(epoll, reactor)
     */
    ServerInterface* server = new ServerClient((struct sockaddr *)&servAddr, serverID);
    /*
     * TODO: Single thread solution
     */
    server->Listen();
    while (server->isAlive() == true)
    {
        server->Accept();
        /**
         * Deal with requests.
         */
        server->ServerMain();
    }

    return 0;
}

bool legalIP(char* str)
{
    struct sockaddr_in tmp;
    int ans = inet_pton(AF_INET, str, &(tmp));
    if (ans <= 0)
        return false;
    return true;
}

bool legalPort(char *str)
{
    int tmp = atoi(str);
    if (!isdigit(tmp))
    {
        return false;
    }
    if (tmp > 65535 || tmp < 1)
        return false;
    return true;
}

bool legalID(char *str)
{
    int tmp = atoi(str);
    if (!isdigit(tmp))
    {
        return false;
    }
    if (tmp > 9999 || tmp < 0)
    {
        return false;
    }
    return true;
}