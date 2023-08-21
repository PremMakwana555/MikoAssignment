# File Based Database

This is a simple file-based one table database. It stores data in a single text file, and the metadata about the table is stored in a separate file. 

## Prerequisites
* Java 17

* Docker

## Building this project

To build the project, run the following command:

``
./gradlew build
``

This will create a JAR file in the build directory.
## Running this project

To run the project, run the following command:

``
./gradlew bootrun
``

This will start the application in local.

## Api's

### Query Execution Api
* ``
localhost:8080/run
``

This is a `POST` request and requires a SQL Query in request body

It accepts three queries 

##### Create
ex : ``CREATE TABLE Person (PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255),
City varchar(255) );``

##### Insert
ex : ``INSERT INTO Person (PersonID, LastName, FirstName, Address, City) VALUES (1, 'Doe', 'jim', '123 Main Street', 'Anytown');``

##### Select
ex : ``SELECT * From Person;``


### Query Execution Api
* ``
  localhost:8080/run
  ``

This is `GET` request, it shows the history from Redis about the previous Queries and their Execution status

## Docker Compose

To Run Entire Application in docker along with redis, make sure following steps should be run first

* [Build Application](README.md#building-this-project)
* Make sure docker is running
* Make sure docker compose is installed

### Run
` docker-compose -f compose-with-app.yml up
`

## Postman Collection 
[Postman Collection](Assignment.postman_collection.json)
