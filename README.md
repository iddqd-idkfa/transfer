# Transfer REST API

Money transfer rest api

Works in-memory, without any secondary in memory db such as H2.

Simplistic single-file API.

Creates standalone jar `transfer-1.0-SNAPSHOT-with-all-dependencies.jar` when `maven package` is run,
does not need any running servers or containers.

#### Uses:

For API:

- Java 8
- Eclispe Vert.x
- Maven

For testing:

- JUnit
- REST-assured

#### To compile:

`mvn compile`

#### To get jar:

`mvn clean`
then 
`mvn package`

jar is generated under `target/` and can be run with `java -jar target/transfer-1.0-SNAPSHOT-with-all-dependencies.jar`

## Endpoints & Usage

### Account

#### Fetch all

`GET localhost:8000/accounts`

#### Fetch One by id

`GET localhost:8000/accounts/1`

#### Create

```
POST localhost:8000/accounts
{
        "name": "Some user",
        "balance": "9876",
        "currency": "ILS"
}
```

#### Update

```
PUT localhost:8000/accounts/0
{
    "name": "New Name",
    "currency": "NEW_CURRENCY"
}
```

#### Delete

Returns 204 on successful delete operation

```
DELETE localhost:8000/accounts/0
```
### Transfer

#### Fetch all

Returns array of json objects

`GET localhost:8000/transfers`

#### Fetch One by id

Returns json of a single transfer resource

`GET localhost:8000/transfers/1`

#### Create (pre-execution)

Returns status 200 on success.

```
POST localhost:8000/transfers
{
    "from": "0",
    "to": "1",
    "amount": "999",
    "currency": "ILS",
    "comment": "Mazel Tov!"
}
```

#### Execute a previously added transfer

Returns status 200 on success.

Success of the operation depends on balances of from/to accounts, currency equivalence 
between accounts, and the condition that the transaction was not executed previously.

```
PUT localhost:8000/transfers/0
```