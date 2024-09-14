+++
title= "Athena"
tags = [ "athena", "aws", "cloud" ]
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
weight= 1
bookCollapseSection= true
+++

# AWS Athena Documentation and Concepts to Learn

## Overview of AWS Athena
AWS Athena is a serverless, interactive query service that enables users to analyze large datasets stored in Amazon S3 using standard SQL. Powered by the open-source Presto query engine, Athena requires no infrastructure management, and you only pay for the queries you run.

## Core Concepts of AWS Athena

### Data Storage in S3
- **Amazon S3** is Athena’s primary data store. Athena queries data directly in S3 in formats such as CSV, JSON, Parquet, ORC, and Avro.
- Understanding how to structure and store data in S3 is crucial for efficient querying.

### Schema-on-Read
- Athena uses a **Schema-on-Read** approach, meaning the schema is defined at the time of query execution rather than when data is written. This provides flexibility in querying raw data without preprocessing.

### SQL-Based Queries
- Athena supports **ANSI SQL** for querying, filtering, joining, and analyzing datasets in S3. Familiarity with SQL is essential to make the most of Athena.

### Partitions
- **Data partitioning** improves query performance by reducing the amount of data scanned. You can partition tables in Athena by columns like date, region, or category. Partitions are added when creating or altering tables.

### Catalogs and Databases
- Athena uses the **AWS Glue Data Catalog** as its metadata store. You define databases and tables in Athena through Glue, and each table points to an S3 location with associated schema information.

### Supported File Formats
- Athena supports a variety of file formats, including:
  - **CSV**
  - **JSON**
  - **Parquet** (columnar format for efficient querying)
  - **ORC** (Optimized Row Columnar)
  - **Avro**
  
  Formats like **Parquet** and **ORC** offer better performance due to their columnar structure and compression capabilities.

### Performance Optimization
- **Data Partitioning**: Organize data by common filter fields like date or region.
- **Compression**: Store data in compressed formats (e.g., GZIP, Snappy) to reduce scan size.
- **Columnar Formats**: Use formats like Parquet or ORC to optimize query performance.

### Cost Management
- Athena charges are based on the amount of data scanned per query. To reduce costs:
  - Store data in **compressed** formats.
  - Use **partitioning** to minimize scanned data.
  - Design queries to retrieve only necessary columns and partitions.

### Security
- **AWS Identity and Access Management (IAM)** controls access to Athena and S3.
- You can encrypt data in S3 using **Server-Side Encryption (SSE)** or **Client-Side Encryption (CSE)**. Athena results can also be encrypted.
- **VPC Endpoints** allow secure querying without exposing data to the internet.

### AWS Glue Integration
- **AWS Glue Data Catalog**: Athena integrates with Glue, which stores metadata (tables, schemas). Glue crawlers can automatically discover and catalog datasets in S3.
- **ETL with AWS Glue**: AWS Glue can be used for extract, transform, and load (ETL) processes, after which you can query the data with Athena.

### User-Defined Functions (UDFs)
- Athena supports **UDFs**, enabling the creation of custom SQL functions for advanced data processing.

### Integration with Other AWS Services
- **Amazon QuickSight**: Athena integrates with QuickSight for interactive dashboards.
- **AWS Lambda**: Lambda functions can be triggered by Athena query results.
- **Amazon CloudWatch**: Athena logs query execution and performance metrics in CloudWatch for monitoring.

### Federated Queries
- **Athena Federated Query** allows querying data across multiple data sources (e.g., RDS, Redshift) and custom data sources alongside S3.

### Handling Unstructured and Semi-Structured Data
- Athena supports **semi-structured data** formats such as **JSON** and **XML** and provides SQL operations to handle **nested data structures** like arrays and maps.

