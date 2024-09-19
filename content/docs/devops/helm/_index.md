+++
title= "Helm"
tags = [ "helm", "devops" ]
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

# Key Concepts to Learn in Helm

## 1. Helm Basics
Understanding what Helm is and its purpose in Kubernetes.

### Learn about:
- **What is Helm?**: Package manager for Kubernetes.
- **Helm Chart**: Pre-configured Kubernetes resources packaged together.
- **Helm Release**: A deployed instance of a Helm chart.
- **Helm Repositories**: Storage for Helm charts that can be downloaded and shared.

## 2. Helm Architecture
Understanding Helm’s core components and how they interact.

### Learn about:
- **Helm Client**: CLI that developers use to manage charts and releases.
- **Helm Chart Repository**: Storage for charts accessible by Helm CLI.
- **Kubernetes Cluster**: Where the Helm release is deployed.
- **Tiller (Helm 2)**: Server-side component that interacts with Kubernetes (removed in Helm 3).
  
## 3. Working with Helm Charts
Learn how to create, manage, and deploy Helm charts.

### Learn about:
- **Helm Create**: Command to scaffold a new chart (`helm create <chart-name>`).
- **Chart.yaml**: Metadata file that defines chart version, name, and other configurations.
- **Values.yaml**: Default configuration values for a chart.
- **Templates/ Directory**: Contains Kubernetes YAML files with Go templates for dynamic values.

## 4. Installing and Managing Releases
How to install, upgrade, and manage Helm releases.

### Learn about:
- **Helm Install**: Install a chart into Kubernetes (`helm install <release-name> <chart-name>`).
- **Helm Upgrade**: Update an existing release with a new chart version (`helm upgrade <release-name> <chart-name>`).
- **Helm Rollback**: Roll back a release to a previous version (`helm rollback <release-name> <revision>`).
- **Helm Uninstall**: Remove a release from the cluster (`helm uninstall <release-name>`).

## 5. Helm Values and Overrides
Customize and override default values in a chart.

### Learn about:
- **Values.yaml**: Customize Kubernetes resources in a chart.
- **Helm Install with Custom Values**: Pass a custom values file (`helm install <release-name> -f <custom-values.yaml>`).
- **Set Flag**: Override values directly from the command line (`--set key=value`).
- **Helm Secrets**: Manage sensitive data like passwords in values (`helm secrets`).

## 6. Chart Dependencies
Managing dependencies in Helm charts.

### Learn about:
- **requirements.yaml** (Helm 2) or **Chart.yaml** (Helm 3): Define chart dependencies.
- **Helm Dependency Update**: Pull down missing dependencies (`helm dependency update`).
- **Managing External Charts**: Install charts that depend on other charts (e.g., databases).

## 7. Helm Hooks
Learn how to use Helm hooks to automate actions during release lifecycle events.

### Learn about:
- **Pre-install and Post-install Hooks**: Tasks that run before or after a chart is deployed.
- **Upgrade and Rollback Hooks**: Automate tasks during upgrades or rollbacks.
- **Delete Hooks**: Run jobs or clean up resources after deletion (`pre-delete`, `post-delete`).

## 8. Helm Templating Engine
Learn about Helm’s templating system, which allows dynamic configuration of Kubernetes resources.

### Learn about:
- **Go Templating Language**: Helm uses Go templates for templating Kubernetes YAML files.
- **Helm Functions**: Built-in and custom functions to manipulate data in templates (e.g., `default`, `include`).
- **Conditionals**: Use `if`, `else`, and `with` statements in templates for dynamic values.
- **Loops**: Iterate over lists and maps using `range`.

## 9. Helm Repositories
Hosting and using Helm repositories to distribute charts.

### Learn about:
- **Helm Repository Add**: Add a new Helm repository (`helm repo add <repo-name> <repo-url>`).
- **Helm Repository Update**: Sync local cache with the Helm repository (`helm repo update`).
- **Helm Push/Publish**: Push a chart to a remote Helm repository.
- **ChartMuseum**: Example of a Helm repository service for hosting private/public charts.

## 10. Helm Best Practices
Developing best practices for Helm chart development and deployment.

### Learn about:
- **Chart Versioning**: Properly version your charts using SemVer.
- **Managing Values.yaml**: Keep default values minimal and use environments for overrides.
- **Reproducible Releases**: Ensure that Helm releases are reproducible across environments.
- **Testing Charts**: Use `helm test` to ensure deployments are stable and functioning.

## 11. Helmfile and Helm Secrets
Advanced tools for managing Helm deployments at scale.

### Learn about:
- **Helmfile**: A tool to manage Helm releases in a declarative manner.
- **Helm Secrets**: Encrypt secrets within your values.yaml file using tools like `sops`.
- **Helm Diff**: Compare Helm chart changes before upgrading.

## 12. Helm 3 Changes (No Tiller)
Understand the differences between Helm 2 and Helm 3.

### Learn about:
- **No Tiller in Helm 3**: No need for a server-side component (Tiller).
- **Improved Security**: Helm 3 removes the need for cluster-wide permissions.
- **Namespace-scoped Releases**: Releases are scoped to namespaces in Helm 3.
- **Better CRD Support**: Helm 3 handles CRDs better during upgrades.

## 13. Helm Testing
How to validate Helm charts before and after deployment.

### Learn about:
- **helm test**: Command to run chart tests defined in the `templates/tests` directory.
- **Unit Testing**: Validate Helm templates with tools like Helm Unit or testing frameworks.
- **Linting Charts**: Use `helm lint` to detect issues in chart configuration.

## 14. Advanced Helm Concepts
Explore more advanced features of Helm.

### Learn about:
- **Subcharts**: Embedding one chart within another as a subchart.
- **Chart Releaser**: Automate releasing of Helm charts using GitHub actions.
- **Helmfile**: Manage Helm charts declaratively with Helmfile.
- **Plugins**: Extend Helm functionality with custom plugins (`helm plugin`).
