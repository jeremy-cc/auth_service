# Authentication

This is a lightweight replacement for the /authentication and /authorisation routes in crm engine, 
implemented in spring-boot.

Technologies:

* amqp (rabbitmq)
* rdbms (mysql / aurora)

This project is built and packaged via gradle.  Run `gradle bootRun` to launch the service.

Authentication makes use of the sugarcrm_${env} database, and performs CRUD against the tables

    Contacts
    ContactsCstm
    Accounts
    AccountCstm
    AccountsContacts
    
It maintains exchanges on rabbitmq, and sources and sinks events for

    Successful Authentication
    Failed Authentication
    Contact exceeded Authentication attempts
    
    


