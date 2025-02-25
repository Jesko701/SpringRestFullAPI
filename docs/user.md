# User API Spec

## Register User
- Endpoint: POST /api/users
Request Body : 
```json
{
    "username" : "khannedy",
    "password" : "rahasia",
    "name" : "Eko Kurniawan Khannedy"
}
```

Response Body (Success):
```json
{
    "data": "OK"
}
```

Response Body (Failed):
```json
{
    "errors" : "username or password invalid"
}
```

## Login User
- Endpoint : POST /api/auth/login
Request Body :
```json
{
    "username":"khannedy",
    "password":"rahasia"
}
```

Response Body (Success) : 
```json
{
    "data" : {
        "token" : "Token",
        "expiredAt" : 2347248724 // millisecond
    }
}
```

Response Body (Failed, 401) : 
```json
{
    "errors":"username or password wrong"
}
```

## Get User
Endpoint: GET /api/users/current

Request Header: 
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) : 
```json
{
    "data" : {
        "username" : "khannedy",
        "name" : "Eko kurniawan Khannedy"
    }
}
```

Response Body (Failed, 401) : 
```json
{
    "errors":"Unauthorized"
}
```

## Update User
Endpoint: PATCH /api/users/current

Request Header: 
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
    "name" : "Eko Khannedy", // put if only want to update name
    "password" : "new password" // put if only want to update password
}
```

Response Body (Success) : 
```json
{
    "data" : {
        "username" : "khannedy",
        "name" : "Eko Khannedy"
    }
}
```

Response Body (Failed, 401) : 
```json
{
    "errors":"Unauthorized"
}
```

## Logout User
Endpoint : DELETE /api/auth/logout

Request Header: 
- X-API-TOKEN : Token (Mandatory)