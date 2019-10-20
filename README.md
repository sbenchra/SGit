# SGit is a 5th year engineering school project [![Build Status](https://travis-ci.com/sbenchra/SGit.svg?token=Qy2HXJgdLDwGvabDRcAG&branch=master)](https://travis-ci.com/sbenchra/SGit)

The project below is  a an implementation of git using scala language and respecting the functional programming best practices and patterns.


<!-- GETTING STARTED -->
## Getting Started

To use the application you must follow the following convention.

### Prerequisites



* Java

```sh
sudo apt-get update
sudo apt-get install default-jdk
```
* Scala version 2.12.7

* Sbt plugin version 0.14.6
```sh
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt
```

### Installation

1. Clone the repo
```sh
git clone https://github.com/sbenchra/SGit.git
```
3. Execute the build script
```sh
source build.sh
```
4. You can use the sgit command in the current shell window by using the following syntax
```
sgit command <arguments>
```
### Commands

1.  sgit init :blush:
2.  sgit add <files names> :blush:
3.  sgit commit -m <message> :blush:
4.  sgit diff :blush:
5.  sgit status :blush:
6.  sgit tag <tagname> :blush:
7.  sgit branch <branchname> :blush:
8.  sgit branch --av :blush:
9.  sgit log --p :blush:
10. sgit log :blush:
11. sgit checkout <tagname branchname commitSha1> :blush:
12. sgit merge :disappointed:
13. sgit rebase :disappointed:






