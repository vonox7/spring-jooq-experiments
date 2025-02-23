# Project setup
1. Get an empty postgres database. You can create one with:
```
$ psql postgres
postgres=# CREATE DATABASE spring_jooq_experiments;
postgres=# GRANT ALL PRIVILEGES ON DATABASE spring_jooq_experiments TO postgres;
```

2. Then configure your database with the following env vars:
* `DB_URL`, defaults to `jdbc:postgresql://localhost:5432/spring_jooq_experiments`
* `DB_USER`, defaults to `postgres`
* `DB_PASSWORD`, defaults to `postgres`

3. Run the project with `./gradlew bootRun`.