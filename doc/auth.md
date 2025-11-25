# Authentication API

To make use of the API you have to be authenticated. This document provides information about the [Authentication API](#api-documentation).

The [Authentication API](#api-documentation) is available under the path `/auth`. It is the only api that does not require any type of authentication.

> [!CAUTION]
> The Authentication API relies on a secure transmition protocol. If you don't use `HTTPS` the passwords and refresh tokens are sent as clear text throgh the internet. This why the others could intercept the comunication and steal the credentials. So make sure to use encryption.

## The Authentication Workflow

1. If you don't already have an user account registered, you have to register a new account using the [`/auth/register`](#authregister) api endpoint.
2. To authenticate yourself as an user you have to use the [`/auth/login`](#authlogin) endpoint. Here you have to provide your user credentials. If your credantials are correct you get an `authorization token` and a `refresh token`.
3. Now that you have an `authorization token` you can use it to access other APIs like `/api/v1`. The way you do that is by appying the authorization token in the HTTP header of your request (`Authorization: Bearer <TOKEN>`).
4. If your authorization token is expired your `refresh token` might not. In This case you can use the refresh token to get a new authentication token using the [`/auth/refresh`](#authrefresh) endpoint.


## API Documentation

### /auth/register

This endpoint allows you to register new users by sending a post request with the required [`RegisterRequest`](#registerrequest) json containing the userdetails.

If the `RegisterRequest` json is valid the Server responses with `200 OK` and sends back a [`RegisterResponse`](#registerresponse) containing a [`Status Code`](#status-codes) and a status message.

If there is a problem with teh `RegisterRequest` like missing fields then the server responses with `400 BAD REQUEST`.

**Example Request**
```bash
curl http://example.com/auth/register \
    -X POST \
    -H "Content-Type: application/json" \
    -d '{"username": "user", "email": "user@mail.com", "password": "pass123"}'
```

**Example Response**
```json
{
    "statusCode": 0,
    "statusMessage": "Successfully registered user."
}
```

### /auth/login

This endpoint allows you to request an `authorization token` you can then use to authorize yourself in future requests. To do so you have to provide a [`LoginRequest`](#loginrequest) object with your user credentials.

If the `LoginRequest` json is valid the Server responses with `200 OK` and sends back a [`AuthResponse`](#authresponse) containing a the `autorization token` and `refresh token` as well as a [`Status Code`](#status-codes) and a status message.

If there is a problem with the `LoginRequest` like missing fields then the server responses with `400 BAD REQUEST`.

**Example Request**
```bash
curl http://example.com/auth/login \
    -X POST \
    -H "Content-Type: application/json" \
    -d '{"identifier": "user", "identifierType": "username", "password": "pass123"}'
```

**Example Response**
```json
{
    "token": "<TOKEN>",
    "refreshToken": "<REFRESH TOKEN>",
    "statusCode": 0,
    "statusMessage": "Login successful."
}
```

### /auth/refresh

This endpoint allows you to request a new `authorization token` without providing your user credentials again. The `refresh token` lasts much longer than the `authorization token`. You have to send a [`RefreshRequest`](#refreshrequest) and get an [`AuthResponse`](#authresponse) in return.

If the `RefreshRequest` json is valid the Server responses with `200 OK` and sends back a [`AuthResponse`](#authresponse) containing a the `autorization token` and `refresh token` as well as a [`Status Code`](#status-codes) and a status message.

If there is a problem with the `RefreshRequest` like missing fields then the server responses with `400 BAD REQUEST`.

**Example Request**
```bash
curl http://example.com/auth/refresh \
    -X POST \
    -H "Content-Type: application/json" \
    -d '{"refreshToken": "<REFRESH TOKEN>"}'
```

**Example Response**
```json
{
    "token": "<TOKEN>",
    "refreshToken": "<REFRESH TOKEN>",
    "statusCode": 0,
    "statusMessage": "Login successful."
}
```

## Data Transfer Objects

Data Transfer Objects (DTOs) are Objects that are being sent between the server and the clinet. In the backend they are represented using Java objects but on transmition they are serialized as Json objects.

### LoginRequest

- **`identifier`** (String): This is the identifier the server uses to determine the right user instance. For example it could be the username of the user.
- **`identifierType`** (String): This has to be either `"email"` or `"username"`, otherwise the server responses with `400 BAD REQUEST`.
- **`password`** (String): The password of the user.

### RefreshRequest

- **`refreshToken`** (String): The refresh token.

### RegisterRequest

- **`username`** (String): Username of the new user. This has to be unique. **Maximum length is 63**.
- **`email`** (String): The email address of the user. This also has to be unique. **Maximum length is 127**.
- **`password`** (String): The password of the user. **Maximum length is 255**.

### AuthResponse

- **`token`** (String): The authorization token for the user. Might be `null` or empty string if `statusCode != 0`.
- **`refreshToken`** (String): The refresh token for the user. Might be `null` or empty string if `statusCode != 0`.
- **`statusCode`** (Integer): The [status code](#status-codes) for the request.
- **`statusMessage`** (String): A human readable explanation of the current response status.

### RegisterResponse

- **`statusCode`** (Integer): The [status code](#status-codes) for the request.
- **`statusMessage`** (String): A human readable explanation of the current response status.


## Status Codes

Status codes are numeric values that represent a specific status. It can be used to determine the success or problem of a request.

> [!NOTE]
> This project makes use of to different types of status codes. The first ones are simply the `HTTP Status Codes` they are used when a request is unauthorized or malformed. The second ones are send with the json and are used to determine the specific problem while handeling the request.

| Status Code | Meaning                     |
| :---------: | :-------------------------- |
| 0           | Success                     |
| 1           | Username Already Taken      |
| 2           | Email Already Taken         |
| 3           | User Not Found              |
| 4           | Invalid Credentials         |

In the backend the `Status Codes` are represented as a enumeration.