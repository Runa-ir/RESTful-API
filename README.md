# RESTful-API
RESTful API for money transfer between accounts.

Frameworks used:

- Server: Jetty
- Rest requests: Jersey 
- In-memory database: H2 / Hibernate

base url: http://localhost:7070
GET /account – list of accounts
GET /account/{id} – details of account {id}
GET /account/{id}/balance – balance of account {id}

PUT /account/{id1}/transfer/{id2}/{amount} – transfer {amount} from account {id1} to account {id2}
PUT /account/{id}/withdraw/{amount} – withdraw {amount} from account {id}
PUT /account/{id}/deposit/{amount} – deposit {amount} to account {id}

GET /holder – list of account holders
GET /holder/{id} – details of account holder {id}
