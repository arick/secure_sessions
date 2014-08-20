
#To create the traditional private key

openssl genrsa -out privkey.pem 2048

# Now convert the traditional private key to a PKCS8 formated private key
openssl pkcs8 -topk8 -inform pem -in privkey.pem -outform pem -nocrypt -out privkey.pkcs8.pem



