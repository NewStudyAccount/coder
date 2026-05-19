@Autowired
private SnowflakeIdWorker snowflakeIdWorker;

long id = snowflakeIdWorker.nextId();
