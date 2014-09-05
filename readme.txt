
#To create the traditional private key

openssl genrsa -out privkey.pem 2048

# Now convert the traditional private key to a PKCS8 formated private key
openssl pkcs8 -topk8 -inform pem -in privkey.pem -outform pem -nocrypt -out privkey.pkcs8.pem


Set up Tomcat Server

<Valve className="co.ssessions.valve.SecureSessionsValve" />

<Manager className="co.ssessions.couchbase.CouchbaseSecureSessionsManager"
         configFilePath="" <!-- Path and filename to the configuration properties file.  Only the filename is needed if the properties file is on the classpath. -->
         />


