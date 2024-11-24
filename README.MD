docker run --name amigoscode-api --rm -p 8080:8080 `
>> --network spring-boot-example_db prista/amigoscode-api `
>> --spring.datasource.url=jdbc:postgresql://db:5432/customer

--rm >> delete the container when it exits
--p 8080:8080 >> expose 8080 of the HOST inside of the Container.

//todo: make use of Vault


docker compose up -d
docker compose down/stop