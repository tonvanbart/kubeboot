.PHONY: image
image: target/kubeboot-0.0.1-SNAPSHOT.jar
	docker build -t kubeboot:minikube --build-arg JAR_FILE=target/kubeboot-0.0.1-SNAPSHOT.jar .

target/kubeboot-0.0.1-SNAPSHOT.jar:
	mvn clean package
