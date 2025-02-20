# library BackEnd 

in order to run the application, 
first, you need to update three field in (src/main/resources/application.properties):
1- spring.datasource.username=sa
2 -spring.datasource.password=12345678
3- openai.api.key= I will send it by mail

second, you need to reload all Maven projects incrementally.
you can do this in 2 ways, going to maven section and click on 
<img width="1440" alt="image" src="https://github.com/user-attachments/assets/ff642833-6fcd-40bd-b7af-fbbb72cb8a97" />

or you can run maven configuration as the image below:
<img width="1440" alt="image" src="https://github.com/user-attachments/assets/1785656e-657e-4ff8-ad39-f91c4b266179" />

after that we build and get all the needed dependencies, you can go to the (src/main/java/com/example/library/LibraryApplication.java) class and run the main.

*** if both backend and frontend projects runs successfully you can view the books details in the UI using this url: http://localhost:3000 


get APIs to test manullay
- GET - http://localhost:8080/books

- POST - http://localhost:8080/books + include in the body the book details as JSON. 
for instance: 
"title": "Spring Boot in Action",
  "author": "Craig Walls",
  "isbn": "9781617294945",
  "publicationYear": 2018,
  "description": "A comprehensive guide to Spring Boot."

- GET http://localhost:8080/books/search?title =name or http://localhost:8080/books/search?author =name
- DEL/GET  http://localhost:8080/books/{book-id}
- PUT http://localhost:8080/books/{book-id}, you should provide the updated details in the body
- 
