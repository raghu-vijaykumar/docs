+++
title= "EKS"
tags = [ "eks", "aws", "cloud" ]
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


Amazon EKS Documentation
Overview
Amazon EKS (Elastic Kubernetes Service) is a fully managed service that makes it easy to run Kubernetes on AWS without needing to install and operate your own Kubernetes control plane. EKS automates the deployment, scaling, and management of Kubernetes clusters, ensuring high availability and security.

Key Features:
Managed Kubernetes Control Plane: AWS handles the management of the Kubernetes control plane, including updates and patches.
Integration with AWS Services: Seamless integration with other AWS services such as IAM, VPC, and CloudWatch.
Scalable: Automatically scales based on the number of nodes and workloads.
Secure: Provides built-in security features including network isolation and encryption.
Core Concepts
1. Clusters
Definition: A Kubernetes cluster in EKS consists of a control plane and a set of worker nodes. The control plane is managed by AWS, while the worker nodes are managed by the user.

Creation: You can create an EKS cluster using the AWS Management Console, AWS CLI, or AWS SDKs.

Example (AWS CLI):

bash
Copy code
aws eks create-cluster --name my-cluster --role-arn arn:aws:iam::account-id:role/eks-service-role --resources-vpc-config subnetIds=subnet-12345678,securityGroupIds=sg-12345678
2. Node Groups
Definition: Managed or self-managed groups of EC2 instances that serve as worker nodes in the cluster.

Types:

Managed Node Groups: AWS manages the lifecycle of these nodes, including updates and scaling.
Self-Managed Node Groups: Users manually manage the lifecycle and scaling of these nodes.
Example (AWS CLI):

bash
Copy code
aws eks create-nodegroup --cluster-name my-cluster --nodegroup-name my-nodegroup --node-role arn:aws:iam::account-id:role/eks-nodegroup-role --subnets subnet-12345678 --instance-types t3.medium
3. Control Plane
Definition: The set of components that manage the state of the Kubernetes cluster, including the API server, etcd (key-value store), and controllers.
Management: AWS manages the control plane, ensuring high availability, scalability, and security.
4. Kubernetes Resources
Definition: Various objects managed by Kubernetes such as Pods, Deployments, Services, and ConfigMaps.
Usage: Deploy and manage containerized applications within the EKS cluster using Kubernetes APIs and tools.
5. IAM Roles and Policies
Definition: AWS IAM roles and policies control access to EKS and its resources.

Usage: Define permissions for users and services to interact with the EKS cluster and its components.

Example Policy:

json
Copy code
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "eks:DescribeCluster",
        "eks:ListClusters"
      ],
      "Resource": "*"
    }
  ]
}
6. VPC and Networking
Definition: EKS integrates with AWS VPC to manage network resources and ensure secure communication between nodes and services.
Components:
Subnets: Public and private subnets where worker nodes and services are deployed.
Security Groups: Control inbound and outbound traffic to instances.
7. Service Discovery
Definition: Allows services within the cluster to find and communicate with each other.
Usage: Kubernetes DNS and service discovery mechanisms are used for inter-service communication.
8. Logging and Monitoring
Definition: Tools and services for tracking and analyzing the performance and health of the cluster and applications.

Integration: AWS CloudWatch and AWS X-Ray can be used for logging and monitoring EKS clusters.

Example (AWS CLI):

bash
Copy code
aws logs create-log-group --log-group-name /aws/eks/my-cluster/cluster
9. Scaling
Definition: Mechanisms to adjust the number of nodes and the resources available to applications based on demand.
Types:
Cluster Autoscaler: Automatically adjusts the number of worker nodes in the cluster.
Horizontal Pod Autoscaler: Automatically scales the number of pod replicas based on CPU and memory usage.
Security and Access Control
1. IAM for Kubernetes
Definition: Integrates AWS IAM with Kubernetes RBAC (Role-Based Access Control) to manage access and permissions.
Usage: Define roles and permissions for users and services using IAM roles and Kubernetes RBAC policies.
2. Network Policies
Definition: Define rules for controlling network traffic between pods.
Usage: Implement network segmentation and security within the cluster.
3. Encryption
Data Encryption: Encrypts data at rest using AWS KMS and in transit using TLS.
Control Plane: Managed by AWS, ensuring encryption and secure communication.
4. Secrets Management
Definition: Manage sensitive information such as passwords and API keys securely.
Usage: Use Kubernetes Secrets and AWS Secrets Manager to handle sensitive data.
Pricing
1. Cluster Costs
Charges: Based on the number of clusters and the resources used by the control plane.
Pricing Model: Charges are based on the number of active clusters and the number of managed node hours.
2. Node Costs
Charges: Based on the EC2 instance types and the number of instances in the node group.
Pricing Model: Pay for the EC2 instances, EBS volumes, and any other resources used.
3. Data Transfer Costs
Charges: Based on the amount of data transferred in and out of the cluster.
4. Additional Costs
Storage and Data Transfer: Costs associated with data storage and transfer within the cluster.
Best Practices
Use Managed Node Groups: Simplify management and scaling of worker nodes using managed node groups.
Implement Security Best Practices: Use IAM roles, network policies, and encryption to secure your cluster and applications.
Monitor and Analyze: Use AWS CloudWatch and other monitoring tools to track cluster performance and health.
Automate Scaling: Implement autoscalers to manage resource usage and adjust to changing workloads.
Regular Updates: Keep your cluster and worker nodes updated to the latest Kubernetes versions and security patches.
Amazon EKS provides a powerful and scalable platform for running Kubernetes clusters on AWS, handling much of the complexity of managing the control plane and allowing you to focus on deploying and managing containerized applications.