## docker-compose and Kubernetes examples

This is a very basic Spring Boot app which stores floating point values in a Redis key/value store.
To add a value (with the app running on port 8080) using [HttpIE](http://httpie.org):

    ~ $ http -f post localhost:8080/value/foo?value=10
    
    HTTP/1.1 201
    Content-Length: 0
    Date: Thu, 03 Jan 2019 21:42:11 GMT
    
Reading a value back:

    ~ $  http localhost:8080/value/foo
    
    HTTP/1.1 200
    Content-Type: application/json;charset=UTF-8
    Date: Thu, 03 Jan 2019 21:42:21 GMT
    Transfer-Encoding: chunked
    
    10.0
    
The app includes the Spring Actuator endpoints, for example:

    ~ $ http localhost:8080/actuator/health
     
    HTTP/1.1 200
    Content-Type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
    Date: Thu, 03 Jan 2019 22:11:25 GMT
    Transfer-Encoding: chunked
    
    {
        "status": "UP"
    }
    
If the app is not able to reach Redis, the status will be shown as `DOWN`.
    
### Running in docker-compose

Build the application jarfile:

    mvn clean package
    
Rebuild the container and start the compose:

    docker-compose build
    docker-compose up
    
Note: just `docker-compose up` should work, however it will not rebuild the application container in all cases.
The app will be exposed on port 9999. See [docker-compose.yaml](docker-compose.yaml) for details.

One point to note is that the port forward on the `keyvalue` service is not necessary for the compose to work; the port
would be exposed on the internal network anyway. However forwarding it means that you can connect locally using `redis-cli`
to poke around. 

### Running on Kubernetes

    kubectl apply -f kubernetes/

The Kubernetes deployment uses the Spring Actuator `/health` endpoint as a readiness probe, the pod
will become ready once the connection to Redis is established and the status becomes `UP` (see above).

To get the URL to access on Minikube:

    url=$(minikube service kubebootsvc --url)
    http $url/actuator/health
    http $url/value/foo
    
The Kubernetes deploy creates 2 replicas of the pod, and the code adds a custom header 
`X-Pod-IP` with the
internal IP of the pod that serviced your request (so you can see the load balancing in action):

    http $url/value/foo
    
    HTTP/1.1 200 
    Content-Type: application/json;charset=UTF-8
    Date: Sat, 05 Jan 2019 00:20:20 GMT
    Transfer-Encoding: chunked
    X-Pod-IP: 172.17.0.10
    
    3.14
