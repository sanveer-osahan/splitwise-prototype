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


## Notes:
1. Please find the sample_documents which can be imported in mongo or used as references for request payloads for their respective services
2. The `/settlements/due` will show the pending settlements for the current user against the requested user for a group. In response, -ve amount means debit and +ve means credit against that user.
3. The `/settlements/pay` will show the same output as `/settlements/due` when called, but asynchronously update the settlements in the database.
4. Refer @CompoundIndexes on the document entities which will make sure the queries are optimized.
5. For sortOrder request parameter in apis, use either "ASC" or "DESC" values. By default, it will consider "DESC"