ngtablejava: This example shows how to use ngTable with 1:n relations with Java backend
========================
Author: Martin Nuc
Level: Intermediate
Technologies: AngularJS, CDI, JPA, EJB, JPA, JAX-RS, ngTable

What is it?
-----------
This example is based on quickstart project by Pete Muir for JBoss with AngularJS found on https://github.com/wildfly/quickstart/tree/master/kitchensink-angularjs with following changes:

- uses AngularJS 1.3.2
- adds 1:n relation between members and cars (Member have multiple cars)
- adds ngTable with bootstrap css.
- adds REST service for ngTable
- shows class MemberFilterer used to provide pagination, column filters and sorting for ngTable
- MemberFilterer uses reflection to be able to filter also by cars name
- removed unused parts

Requirements?
-----------
- JBoss Wildfly 8
- datasource java:jboss/datasources/ngTable