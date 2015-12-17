insert into servicemetadata(id, provider_display_name, display_name, image_url) values(1,'Boundless Spatial', 'Boundless OpenGeo Suites', 'http://boundlessgeo.com/wp-content/uploads/2015/10/Boundless-new-web-logo-e1444657868290.png');
insert into services (name, id, description, bindable, metadata_id) values ('OpenGeo', '58A0E4B0-DFC2-4CD8-827B-94F6E49EA7BC', 'OpenGeo Server Basic Profile', true, 1);

insert into credentials (id, uri, username, password, memory, disk) values ('cred1', 'jhankes/centos-gs', null, null, 2, 4);
insert into credentials (id, uri, memory, disk)                     values ('cred2', 'jhankes/centos-gs', 4, 8);

insert into creds_other_attributes (creds_other_attrib_id, name, value) values ('cred1', 'otherAttribute', 'dummyvalue');
insert into creds_other_attributes (creds_other_attrib_id, name, value) values ('cred1', 'testMemory', '4');

insert into creds_other_attributes (creds_other_attrib_id, name, value) values ('cred2', 'otherAttribute', 'dummyvalue');
insert into creds_other_attributes (creds_other_attrib_id, name, value) values ('cred2', 'testMemory', '10');


insert into planmetadata (id) values(1);
insert into planmetadata (id) values(2);

insert into cost(id, planmetadata_id, unit) values(4, 1, 'MONTHLY');
insert into cost(id, planmetadata_id, unit) values(5, 2, 'WEEKLY');

insert into cost_amounts(cost_amounts_id, value, currency) values(4, 0, 'usd');
insert into cost_amounts(cost_amounts_id, value, currency) values(5, 0, 'usd');


insert into plan_metadata_bullets (plan_metadata_id, bullets) values (1, 'Free, SOAP Service');
insert into plan_metadata_bullets (plan_metadata_id, bullets) values (2, 'Paid, premium SOAP Service');


insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('basic', '1DD7E874-E265-44F1-AD2F-676207016CC3', 'Basic Plan limited to 2GB Memory', '58A0E4B0-DFC2-4CD8-827B-94F6E49EA7BC', 'cred1', 1, 1);
insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('premium', '26847B68-57E6-49E2-BA44-F82DCD7E46AA', 'Premium Plan limited to 4GB Memory', '58A0E4B0-DFC2-4CD8-827B-94F6E49EA7BC', 'cred2', 2, 0);



--insert into servicemetadata(id, provider_display_name, display_name, image_url) values(2, 'Service Provider Inc.', 'Service Provider', '/images/pivotal-img.png');
--insert into services (name, id, description, bindable, metadata_id) values ('PolicyInterface', '2896b732-4587-386a-9a5e-3bde75e57df3', 'Policy Retrieval System', true, 2);

--insert into credentials (id, uri, username, password) values ('cred3', 'http://policy-service.classic.coke.cf-app.com/soap/RetrieveService', null, null);
--insert into credentials (id, uri) values ('cred4', 'http://gold-policy-service.classic.coke.cf-app.com/soap/RetrieveService');

--insert into planmetadata (id) values(3);
--insert into planmetadata (id) values(4);

--insert into cost(id, planmetadata_id, unit) values(101, 3, 'MONTHLY');
--insert into cost(id, planmetadata_id, unit) values(102, 4, 'WEEKLY');

--insert into cost_amounts(cost_amounts_id, value, currency) values(101, 0, 'usd');
--insert into cost_amounts(cost_amounts_id, value, currency) values(102, 10, 'usd');

--insert into plan_metadata_bullets (plan_metadata_id, bullets) values (3, 'Free, SOAP Service');
--insert into plan_metadata_bullets (plan_metadata_id, bullets)  values (4, 'Paid, premium SOAP Service');

--insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('basic', '33febe21-64d6-39f8-aafa-e102e145a98a', 'Basic Plan throttled to 5 connections per second', '2896b732-4587-386a-9a5e-3bde75e57df3','cred3', 3, 1);
--insert into plans (name, id, description, service_id, plan_cred_id, metadata_id, is_free) values ('premium', 'fa3d189a-6298-3089-8c1f-e9144b48e16c', 'Premium Plan throttled to 50 connections', '2896b732-4587-386a-9a5e-3bde75e57df3', 'cred4', 4, 0);



