# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        bigint not null AUTO_INCREMENT,
  name                      varchar(255),
  created                   timestamp,
  constraint pk_project primary key (id))
;

create table prompt (
  id                        bigint not null AUTO_INCREMENT,
  filename                  varchar(255),
  text                      varchar(255),
  file                      blob,
  project_id                bigint,
  constraint pk_prompt primary key (id))
;

--create sequence project_seq;

--create sequence prompt_seq;

alter table prompt add constraint fk_prompt_project_1 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_prompt_project_1 on prompt (project_id);



# --- !Downs

--SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists project;

drop table if exists prompt;

--SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists project_seq;

drop sequence if exists prompt_seq;

