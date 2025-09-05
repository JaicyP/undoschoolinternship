
# Course Search Application

The Course Search Application is a SpringBoot application that:
 
- Indexes a set of sample course documents in to elastic  search at startup

- Exposes a REST endpoint to search courses with multiple filters,pagination and sorting.(Assignment A)

- Autocomplete suggestions and fuzzy matching for course titles.(Assignment-B)

- Exposes a REST endpoint to reindex the course documents in to elasticSearch when needed.

- spins up a local single node with Docker compose, acessible at http://localhost:9200 with out authentication.

- Built using SpringBoot and Elastic Server.



## Tech Stack

JDK 17

SpringBoot 3.3.2

Maven 4.0.0

Docker & Docker Compose

Elasticsearch 8.11.1









## Installation
### Part 1: Start Elasticsearch

Install docker desktop

```bash
  cd my-project
  docker compose up
```

verify:
```bash
  curl http://localhost:9200
```
you should see JSON with the cluster name and version

To stop:
```bash
  docker compose down
```

### Part 2: Run the SpringBoot App

on the first run the app will:

   - create courses index with mappings
   - Read src/main/resources/courses.json(50 samples) and bulk-index them 

   verify Data Ingestion

-To check the indices
```bash
  curl -X GET "http://localhost:9200/_cat/indices?v"
```
- check health status
```bash
  curl -X GET "http://localhost:9200/_cluster/health"
```
-view the mappings for courses index
```bash
  curl -X GET "http://localhost:9200/courses/_mapping"
```
 


## API Reference

#### Get all courses

```http
  GET /api/search
```
#### Search (Assignment A)

```http
  GET /api/search
```
Query params

- q — keyword (full-text across title & description)
- minAge, maxAge — range filter
- category — exact match
- type — exact match (ONE_TIME, COURSE, CLUB)
- minPrice, maxPrice — price range
- startDate — ISO-8601; only sessions on/after date
- sort — upcoming (default), priceAsc, priceDesc
- page — default 0
- size — default 10

For example,
```http
  http://localhost:8080/api/search?q=math&size=5"
```
Response JSON is 

{   
 "total": 123,
  "page": 0,
  "size": 10,
  "courses": [
    {"id":"C-001","title":"Algebra Basics","category":"Math","type":"COURSE","price":49.0,"nextSessionDate":"2025-06-10T15:00:00Z"}
  ]
}

sample API

```http
  GET api/search?q=watercolor,fun,chance
  GET api/search?q=watercolor,fun,chance&sort=priceAsc
  GET api/search?q=cells
  GET api/search?minPrice=50&maxPrice=200
  GET api/search?category=Math&type=COURSE
  GET api/search?type=COURSE
  GET api/search?startDate=2025-07-31T10:00:00Z
  GET api/search?type=COURSE&page=1
```
### Autocomplete suggestions and fuzzy Search (Bonus, Assignment B)

Query params

- q — keyword (title search)

```http
  GET /api/suggest?q=<prefix>
```
#### Autocomplete

sample API

   http://localhost:8080/api/suggest?q=phy

Response:
   
   [
    "Photography Basics",
    "Physics Experiments"
  ]

#### Fuzzy Search

sample API

  http://localhost:8080/api/suggest?q=pysic

Response:
   
   [
    "Physics Experiments"
  ]



