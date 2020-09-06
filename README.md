# Product Service
This Spring boot service will make use of Spring Batch. What is better for high volume files than something which is made for it. I haven't used it in the past, so this assignment gave me the opportunity to dive into this new stuff for me.  
It's not yet perfect... There is a lot of configurations I didn't touch yet, but the output and validation are working. 
The concept of steps in a job is really nice.

### Building the app
From the root:  
```
$ mvn clean install
```  

### Running the APP
From the root folder:  
```
$ mvn clean package spring-boot:repackage
 
$ java -jar target/statementprocessore-0.0.1-SNAPSHOT.jar  
or  
$ mvn spring-boot:run
```
### Properties
There are a few properties you can define. Location of the input and names of the output result files.  
If you want to keep the data you can put the 'app.clear-db' property to true. If you do this there is still a glitch in  the code that the previous jobs are also taken into account with the validation. 

```json
file:
  csv:
    input: "/input/records.csv"
    output: "errorsCsv.json"
  xml:
    input: "/input/records.xml"
    output: "errorsXml.json"
app:
  clear-db: true
```

### H2 console
For development purpose, there is the famous H2 Console. Reachable by http://localhost:8080/h2-console

### Improvements (not yet implemented)
- Use Nio2 WatchService to trigger jobs based on droppings in a folder.
- implement docker/docker-compose.
- Configuration via Env variables.
- At the moment using native SQL over JPA.
