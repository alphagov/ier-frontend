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

 2. Set up repositories to connect to Nexus - **you will need to obtain login credentials for this**:

    * Create a file *~/.sbt/repositories* with the following content:
      `[repositories]
         local
         ier-nexus: https://ci.ertp.alphagov.co.uk/nexus/content/groups/repositories`
      (This will cause SBT to use Nexus for all Maven-style artifacts, but it will continue to use its inbuilt list of default repositories for Ivy-style artifacts)

    * Create a file *~/.sbt/credentials* with the following content:
      `realm=Sonatype Nexus Repository Manager
       host=ci.ertp.alphagov.co.uk
       user=<credentials_username>
       password=<credentials_password>`
      (This supplies the Nexus credentials to access the repository)

    * Create a file *~/.sbt/0.13/plugins/credentials.sbt* with the following content:
      `credentials += Credentials(Path.userHome / ".sbt/credentials")`
      (This will load the Nexus credentials for plugin build)

 3. In a terminal execute `./play` to open the Play console
 
 4. Wait (Downloading the entire internet)
 
 5. In the Play console execute `compile` to compile the app
 
 6. Create directory `/var/log/ier` with write access rights for the current user
    _In my case it was:_  
    _sudo mkdir /var/log/ier_  
    _sudo chown root:users /var/log/ier_  
    _sudo chmod 777 /var/log/ier_

 7. In the Play console execute `run` to start the app
 
 8. Go to `http://localhost:9000/`
    \[Note: I got an internal error had to re- `gem install sass` to fix it \]

 
###Running the service

 1. In a terminal execute `./play` to open the Play console

 2. In the Play console execute `run` to start the app
 
 3. Or just `./play run`
 
 4. go to `http://localhost:9000/`
