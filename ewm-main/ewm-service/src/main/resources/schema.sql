CREATE TABLE IF NOT EXISTS "events" (
                          "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                          "annotation" varchar(2048),
                          "category_id" int,
                          "confirmed_requests" int,
                          "created_on" timestamp,
                          "description" varchar(8000),
                          "event_date" timestamp,
                          "initiator_id" int,
                          "location_id" int,
                          "paid" boolean,
                          "participant_limit" int,
                          "published_on" timestamp,
                          "request_moderation" boolean,
                          "state" varchar(16),
                          "title" varchar(150)
);

CREATE TABLE IF NOT EXISTS "users" (
                         "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         "name" varchar(255),
                         "email" varchar(255) Unique,
                         "is_approved" boolean,
                         CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS "locations" (
                             "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                             "lat" float,
                             "lon" float
);

CREATE TABLE IF NOT EXISTS "categories" (
                              "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                              "name" varchar(64),
                              CONSTRAINT UQ_CAT_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS "compilations" (
                                "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                "title" varchar(64),
                                "pinned" boolean
);

CREATE TABLE IF NOT EXISTS "compilations_events" (
                                       "compilation_id" int,
                                       "event_id" int,
                                       PRIMARY KEY ("compilation_id", "event_id")
);

CREATE TABLE IF NOT EXISTS "participation_requests" (
                                          "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          "created" timestamp,
                                          "event_id" int,
                                          "requester_id" int,
                                          "status" varchar(16),
                                          CONSTRAINT uq_requests UNIQUE(event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS "moderation_events" (
                                                        "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                                        "timestamp" timestamp,
                                                        "event_id" int,
                                                        "previous_state" varchar(16),
                                                        "new_state" varchar(16),
                                                        "comment" varchar(2000)
);

ALTER TABLE "events" ADD FOREIGN KEY ("initiator_id") REFERENCES "users" ("id");

ALTER TABLE "events" ADD FOREIGN KEY ("location_id") REFERENCES "locations" ("id");

ALTER TABLE "events" ADD FOREIGN KEY ("category_id") REFERENCES "categories" ("id");

ALTER TABLE "compilations_events" ADD FOREIGN KEY ("compilation_id") REFERENCES "compilations" ("id");

ALTER TABLE "compilations_events" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id");

ALTER TABLE "participation_requests" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id");

ALTER TABLE "participation_requests" ADD FOREIGN KEY ("requester_id") REFERENCES "users" ("id");

ALTER TABLE "moderation_events" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id");


