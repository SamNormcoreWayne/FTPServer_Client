#pragma once
#ifndef __SERVERFUNCINTERFACE_HPP__
#define __SERVERFUNCINTERFACE_HPP__

#include <vector>
#include <string>

class ServerFuncInterface
{
public:
    ServerFuncInterface() {};
    virtual ~ServerFuncInterface() = 0;
    virtual void list(std::vector<std::string>) = 0;
    virtual void cd(std::vector<std::string>) = 0;
    virtual void get(std::vector<std::string>) = 0;
    virtual void put(std::vector<std::string>) = 0;
    virtual void pwd() = 0;
    virtual void port() = 0;
    virtual void pasv() = 0;
};

#endif
