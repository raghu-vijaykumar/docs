+++
title= "Request/Response Transformation"
tags = [ "api-gateway", "aws", "cloud", "request-response-transformation" ]
author = "Me"
showToc = true
TocOpen = false
draft = false
hidemeta = false
comments = false
disableShare = false
disableHLJS = false
hideSummary = false
searchHidden = true
ShowReadingTime = true
ShowBreadCrumbs = true
ShowPostNavLinks = true
ShowWordCount = true
ShowRssButtonInSectionTermList = true
UseHugoToc = true
weight= 2
bookFlatSection= true
+++

# Request/Response Transformation in Amazon API Gateway

Amazon API Gateway provides **request and response transformation** capabilities, allowing developers to modify, map, and validate incoming API requests and outgoing responses before they reach backend services or clients. This is useful for adapting requests/responses to match the format or structure required by different systems or clients, without needing to change the backend services.

## Key Concepts

### 1. **Mapping Templates**
A **mapping template** in API Gateway is used to transform the request/response payloads between the client and the backend. Mapping templates are defined in the API's integration request/response and are written in **Velocity Template Language (VTL)**.

### 2. **Request Transformation**
Request transformation allows you to modify the incoming API request before it reaches the backend service. This can include tasks like:
- Changing the structure of the request body.
- Adding or removing query parameters, headers, or path variables.
- Mapping client requests to the expected format of the backend service.

### 3. **Response Transformation**
Response transformation allows you to modify the backend response before it reaches the client. This is useful when:
- The backend service returns responses in a format different from what the client expects.
- You want to filter sensitive data in the response.
- You need to restructure or reformat the response.

## Request Transformation Example

**Scenario:**
You have a client that sends a request in the following format:

```json
{
  "firstName": "John",
  "lastName": "Doe"
}
```
But your backend service expects the request in this format:

```json
{
  "name": {
    "first": "John",
    "last": "Doe"
  }
}
```

**Mapping Template (Request Transformation)**
You can create a mapping template to transform the incoming request to the format expected by the backend:

```json
{
  "name": {
    "first": "$input.path('$.firstName')",
    "last": "$input.path('$.lastName')"
  }
}
```

**Step-by-step Process:**
1. **Create the API:** In Amazon API Gateway, create an API with a POST method.
2. **Define the Integration Request:** Go to the Integration Request and add a Mapping Template for the request body.
3. **Specify the Content Type:** Choose application/json as the content type.
4. **Write the Mapping Template:** Use the above template to transform the incoming request body.
5. **Deploy the API:** Deploy the API, and when the client sends a request in the original format, API Gateway will transform it to the new format before sending it to the backend.

**Before Transformation (Client Request):**
```json
{
  "firstName": "John",
  "lastName": "Doe"
}
```

**After Transformation (Sent to Backend):**
```json
{
  "name": {
    "first": "John",
    "last": "Doe"
  }
}
```

## Response Transformation Example

**Scenario:**
Your backend service returns the following response:

```json
{
  "status": 200,
  "data": {
    "userId": "12345",
    "fullName": "John Doe"
  },
  "meta": {
    "timestamp": "2024-09-11T10:00:00Z"
  }
}
```
But the client expects a simplified response in this format:

```json
{
  "user": "John Doe",
  "id": "12345"
}

**Mapping Template (Response Transformation)**
You can create a mapping template to transform the backend response to the format the client expects:

```json
{
  "user": "$input.path('$.data.fullName')",
  "id": "$input.path('$.data.userId')"
}
```

**Step-by-step Process:**
1. **Create the API:** In Amazon API Gateway, create an API with a GET method.
2. **Define the Integration Response:** Go to the Integration Response and add a Mapping Template for the response body.
3. **Specify the Content Type:** Choose application/json as the content type.
4. **Write the Mapping Template:** Use the above template to transform the backend response body.
5. **Deploy the API:** Deploy the API, and the client will receive the transformed response.

**Before Transformation (Backend Response):**
```json
{
  "status": 200,
  "data": {
    "userId": "12345",
    "fullName": "John Doe"
  },
  "meta": {
    "timestamp": "2024-09-11T10:00:00Z"
  }
}
```

**After Transformation (Client Response):**
```json
{
  "user": "John Doe",
  "id": "12345"
}
```

## Advanced Transformations

1. **Mapping Headers**
API Gateway can also map request and response headers. You can add, remove, or modify headers before sending requests to the backend or returning responses to the client.

**Example: Adding a Header to a Request**
You can add a custom header, like an Authorization header, before sending the request to the backend.

```json
#set($authToken = "Bearer " + $input.params('auth'))
{
  "Authorization": "$authToken"
}
```

2. **Mapping Query Parameters**
You can map query string parameters or even construct new query parameters before sending the request to the backend.

**Example: Modifying Query Parameters**
Transforming a query parameter from clientId to userId:

```json
{
  "userId": "$input.params('clientId')"
}
```

3. **Error Mapping**
API Gateway can be configured to map backend errors to custom error messages for the client. For example, you can map a 500 error from the backend to a more user-friendly 400 response.

**Example: Mapping Error Responses**
Mapping a 500 error to a 400 error:

```json
{
  "errorMessage": "There was a problem with your request. Please try again."
}
```

## Use Cases for Request/Response Transformation

1. **Legacy System Integration:** When backend services have different API formats, you can transform requests and responses to maintain compatibility without modifying the backend.
2. **Mobile API Simplification:** You can transform complex backend responses into simpler, more efficient payloads for mobile clients.
3. **Multi-client Support:** If different clients (web, mobile, IoT) need different response formats, request/response transformation can serve each client without modifying the backend.
4. **Security and Privacy:** Mask sensitive data in responses, such as PII (personally identifiable information), before sending the response to the client.
