# blog-render-api

blog-render-api is a part of personal blog engine. Service get json from blog-content-api service and compile it into html page
via handlebars 

# Build
mvn clean package && docker build -t com.alexnerd/blog-render-api .

# RUN

docker rm -f blog-render-api || true && docker run -d -p 8080:8080 -p 4848:4848 --name blog-render-api com.alexnerd/blog-render-api 

# System Test

Switch to the "-st" module and perform:

mvn compile failsafe:integration-test