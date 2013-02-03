jinah-entity
=============

JINAH-ENTITY is a micro library that provides utilitary classes for basic manipulation of JPA entities. 

Some features:

  - intelligent equals and hashcode methods that uses business key;
  - basic Query utilities;
  - and so on.

Version
-

1.2.0_rc1

License
-

Apache 2.0

Some examples
=============
(The getters and setters methods was omitted to make the examples more readable.)

### Using BusinessKey annotation
> to configure the fields that identify a entity as unique.

    @Entity
    @BusinessKey({"lastName", "firstName"})
    public class Person {
        @Id @GeneratedValue
        private Long id;
        private String lastName;
        private String firstName;
        
        @Override
        public equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Person)) {
                return false;
            }
            return BusinessKeyHelper.equals(this, obj);
        }
        
        @Override
        public int hashCode() {
            return BusinessKeyHelper.hashCode(this);
        }
    }
 
### QueryRequest, QueryCommand and QueryResult
> to configure, execute queries and return results.

#### Create and configure a POJO to transport query parameters

Use the *WhereClauseFragments* annotation to configure "WHERE" fragments that will be used to construct dynamically the "SELECT" query with the informed parameters.

    @WhereClauseFragments({
        @WhereClauseFragment(name="id",
                            fragment = "id = :id"),
        @WhereClauseFragment(name="beginDate",  
                            fragment = "startDate = :beginDate", 
                            temporalType = TemporalType.DATE),
        @WhereClauseFragment(name="endDate",
                            fragment = "endDate = :endDate" ,
                            temporalType = TemporalType.DATE)
    })
    public class QueryArgs {
        private Long id;
        private Date beginDate;
        private Date endDate;
    }

#### Configure the QueryRequest

Use the QueryRequest to configure a query to be executed. Can be a "select" statement or a named query.

    String query = "select new Event(id, startDate, endDate, description) from Event";
    List<String> orderBy = Arrays.asList(new String[] { "obj.startDate" });

    QueryArgs args = new QueryArgs();
    args.setBeginDate(new Date());

    QueryRequest queryRequest = new QueryRequest(query, args, orderBy);

    // Configure to retrieve the first 20 records.
    queryRequest.configPartialResult(0, 20, true);

#### Create the QueryCommand

QueryCommand is a utility class to execute a configured QueryRequest. It encapsulates the logic that builds the where clause (if not is a named query), set query parameters, etc.

    EntityManager em = ...
    QueryCommand queryCmd = new QueryCommandImpl(em);
    
    queryCmd.configure(queryRequest, Event.class);

#### Execute the query and return the QueryResult

    QueryResult<Event> result = queryCmd.execute();
    
    List<Event> events = result.getResultList();
    
    int rowsReaded = result.getPartialCount();
    int totalRowsAvailable = result.getTotalCount();