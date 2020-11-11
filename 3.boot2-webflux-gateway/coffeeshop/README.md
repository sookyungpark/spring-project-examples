# coffeeshop
coffeeshop api project

## Usage
### Run Web API
```
./gradlew coffeeshop-api:clean coffeeshop-api:bootRun
```
### [Deprecated] Init Submodules
```
 git remote add coffeeshop-domain https://github.com/projectrepo/coffeeshop-domain.git
 
 git subtree add --prefix=coffeeshop-domain/ coffeeshop-domain develop

 git subtree pull --prefix coffeeshop-domain coffeeshop-domain develop --squash
``` 

## [Deprecated] Subtree
coffeeshop-api uses:

- coffeeshop-domain

##For Documentation

- Write testCase on ApiDocumentationTest, with restDocumentation
- add markups for given test on docs/asciidoc/index.adoc
- build (or bootRun), check http://localhost:8080/docs/index.html