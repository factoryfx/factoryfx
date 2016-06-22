# Factoryfx

*Dependency Injection Framework.*

## Introduction

Factoryfx is a lightweight Dependency Injection Framework.:

when you manually use dependency injection you have 2 types of objects.
* business logic
* Factory code which instantiate the business logic objects
explained here: https://www.youtube.com/watch?v=RlfLCWKxHJ0&index=3&list=PL693EFD059797C21E#t=30m

Most dependency Injection Frameworks automate the Factory code.
Factoryfx takes a different approach by defining a manually created structure for the factories.


* no reflection magic means easy to debug and no supprises at runtime
* you can add configuration data to the factories
* validates at compile-time
* easy Multitenancy support
*

###Configuartion Data
With factoryfx you can add user ediable data to the factories.
Typical configuration data are ports, hostname, ssl certificates but why not add even more data? Data which are typical in the database.
if you look at typical database you 2 types of data.

* basic data e.g Products in a simple shop
* mass data e.g. Orders shop

###Multitenancy

At least develepmont and test. Difference with implemnteted with polymorphy

![Alt text](docu/comparison.png "Optional Title")


## Setup

## Usage

## Contribute


## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

