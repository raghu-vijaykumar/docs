+++
title = "Databases"
tags = [ "database" ]
author = "Me"
showToc = true
TocOpen = false
draft = true
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
+++

# Databases

## ACID Properties

| **ACID Property** | **Description**                                                                                                                        | **Example**                                                                                                                  |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| **Atomicity**     | Ensures that a transaction is treated as a single, indivisible unit. If any part of the transaction fails, it is rolled back entirely. | In a bank transfer, both debit and credit operations must succeed, or neither will be executed.                              |
| **Consistency**   | Ensures that a database moves from one valid state to another after a transaction, following all predefined rules and constraints.     | A transaction that ensures a bank balance never goes negative, even after withdrawals.                                       |
| **Isolation**     | Ensures that the operations within a transaction are not visible to other transactions until the transaction is completed.             | Two users making changes to the same account at the same time won’t interfere with each other, maintaining data consistency. |
| **Durability**    | Ensures that once a transaction is committed, the data is permanently saved and will persist even in case of system failures.          | After a bank transfer, the updated balance is saved, and the data remains intact even if there’s a power outage or crash.    |
