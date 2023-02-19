# OVERVIEW #

This is my application ...

### Pre-requistes ###
* Sbt-1.8.2
* Docker
* Docker-Compose
* Kind + Kubectl

## Packing the application ##
To pack the application you need to jump in both directories `AkkaApi` and `DataLoader` and run the command :

```
$ sbt pack
```

## Running the application with Kubernetes##
### 1- Building the images ###
To build the image of cassandra, we will need to access to the `AkkaApi` directory and run the command :

```
$ docker build -t db:1.0 -f Database.Dockerfile .
```
Then we will build the api image with :

```
$ docker build -t my-akkaapi:1.0 .
```
Then access to the `DataLoader` directory and run the command to build the spark loader image with :

```
$ docker build -t my-spark:1.0 .
```

### 2- Creating cluster ###
To create a cluster, we will use Kind with the command :

```
$ Kind create cluster --name <cluster-name>
```
### 3- Loading the images in Kind cluster ###
To load the images into kind cluster we run the following commands :

```
$ kind load docker-image db:1.0 --name <cluster-name>
$ kind load docker-image my-akkaapi:1.0 --name <cluster-name>
$ kind load docker-image my-spark:1.0 --name <cluster-name>
```
Then set the context to the Kind cluster by running the following command :

```
$ kubectl config use-context kind-<cluster-name>
```
### 4- Applying the Kubernetes manifests ###
1- Jump to the `AkkaApi` directory and run the commands to create cassandra database and the table inside :

```
$ kubectl apply -f cassandra.yml
$ kubectl apply -f load_keyspace.yml
```
2- When cassandra is loaded and and the load_keyspace service finished running, you can run the following command to check if the table is created in cassandra :

```
$ kubectl exec -it <pod_name> -- cqlsh -u cassandra -p cassandra
$ use first;
$ SELECT * FROM mydb;
```
3- You can now apply the dataloader and the api manifests with :

```
$ kubectl apply -f dataloader.yml
$ kubectl apply -f api.yml
```
You can then rerun the command : 

```
$ kubectl exec -it <pod_name> -- cqlsh -u cassandra -p cassandra
$ use first;
$ SELECT * FROM mydb;
```
to check if the data is successfully loaded to cassandra.

4- You can now test the api with :  

```
$ kubectl port-forward svc/api 8080:8080
```

And check this link in your browser : 
http://localhost:8080/data?sku=75&promo_cat=0&promo_discount=0.5





## Run the application with Docker-Compose##
### Run the application ###
To spin up the Containers of the application you just need to access to the `AkkaApi` directory and run :

```
$ docker-compose build cassandra 
```
### Steps to test the application ###
