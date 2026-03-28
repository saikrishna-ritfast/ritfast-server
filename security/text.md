com.sai.crm
├── auth
│ ├── controller
│ ├── dto
│ ├── service
│ └── mapper
├── client
│ ├── entity
│ ├── repository
│ └── service
├── user
│ ├── entity
│ ├── repository
│ └── service
├── role
│ ├── entity
│ ├── repository
│ └── service
├── permission
│ ├── entity
│ ├── repository
│ └── service
├── security
│ ├── jwt
│ ├── config
│ └── userdetails
├── common
│ ├── enums
│ ├── util
│ ├── exception
│ └── response

{
"email": "sai@example.com",
"password": "StrongPassword123"
}

{
"user": {
"id": 1,
"clientId": 101,
"emailId": "sai@example.com",
"firstName": "Sai",
"lastName": "Krishna",
"statusCode": "ACTIVE",
"stringAuthorities": [
"ROLE_OWNER",
"USER_CREATE",
"USER_READ",
"USER_UPDATE",
"CLIENT_READ",
"CLIENT_UPDATE",
"LEAD_CREATE",
"LEAD_READ",
"LEAD_UPDATE",
"LEAD_DELETE"
]
},
"client": {
"id": 101,
"code": "SAICRM",
"name": "Sai CRM",
"typeCode": "BUS",
"levelType": "CLIENT",
"statusCode": "ACTIVE"
},
"accessToken": "jwt-token",
"loggedInClientId": 101,
"loggedInClientCode": "SAICRM"
}
