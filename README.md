#IER Frontend

This is the Individual Electoral Registration Frontend app. The point of it is to provide a simple Register to Vote form to the public.

###Depedencies

To run this app you will need: 

 - Java 7 (we use openJDK 7)
 - Ruby 1.9.3
 - Sass
 - Bundler

All other depedencies (Play framework, Scala, sbt, etc) will be installed as part of the `play` start script.

###Bootstrap project and run the service

 1. `git clone git@github.gds:gds/ier-frontend.git`
 
 2. In a terminal execute `./play` to open the Play console
 
 3. Wait (Downloading the entire internet)
 
 4. In the Play console execute `compile` to compile the app
 
 5. Create directory `/var/log/ier` with write access rights for the current user  
    _In my case it was:_  
    _sudo mkdir /var/log/ier_  
    _sudo chown root:users /var/log/ier_  
    _sudo chmod 770 /var/log/ier_  

 6. In the Play console execute `run` to start the app
 
 7. go to `http://localhost:9000/`

 
###Running the service

 1. In a terminal execute `./play` to open the Play console

 2. In the Play console execute `run` to start the app
 
 3. Or just `./play run`
 
 4. go to `http://localhost:9000/`
