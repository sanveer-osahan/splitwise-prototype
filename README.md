# Splitwise Prototype
A prototype application having features of Splitwise

## Steps for using the service
1. Run the docker services for mongo and kafka  
   ```
   cd docker_services
   sh up.sh
   ```
   For stoping the docker services
   ```
   sh down.sh
   ```


2. Run `SplitwiseApplication`  
   The application will by default run on `localhost:8080`


3. Open <http://localhost:8080/swagger-ui/index.html#> to view and access the endpoints 


4. Except `/ping` and `/users/**`, all the other endpoints are protected by the header `x-user-id`.  
   After creating users, use their ids as value for this header to make it act like an authenticated user is making the api calls


## Note:
Please find the sample_documents which can be imported in mongo  
or used as references for request payloads for their respective services